package lite.scheduler.core.bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.ThreadContext;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import lite.scheduler.core.LiteSchedulerConfiguration;
import lite.scheduler.core.entity.ExecutionHistory;
import lite.scheduler.core.entity.GlobleParameter;
import lite.scheduler.core.entity.JobGroup;
import lite.scheduler.core.entity.JobGroupParameter;
import lite.scheduler.core.entity.JobParameter;
import lite.scheduler.core.entity.Schedule;
import lite.scheduler.core.entity.ScheduleParameter;
import lite.scheduler.core.enums.ExecutionStatus;
import lite.scheduler.core.enums.ExecutionType;
import lite.scheduler.core.enums.ScheduledState;
import lite.scheduler.core.interfaces.ScheduleJob;
import lite.scheduler.core.repository.ExecutionHistoryRepository;
import lite.scheduler.core.repository.GlobleParameterRepository;
import lite.scheduler.core.repository.JobGroupRepository;
import lite.scheduler.core.repository.JobRepository;
import lite.scheduler.core.repository.ScheduleRepository;
import lite.scheduler.core.vo.ExecuteParamenter;
import lite.scheduler.core.web.LiteSchedulerService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final public class InternalScheduledJob implements Job {

	private ApplicationContext applicationContext;

	private LiteSchedulerService litechedulerService;
	private JpaTransactionManager transactionManager;

	GlobleParameterRepository globleParameterRepository;

	private String executionId;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		executionId = UUID.randomUUID().toString();

		this.applicationContext = LiteSchedulerConfiguration.getApplicationContext();
		this.transactionManager = applicationContext.getBean("core_transactionManager", JpaTransactionManager.class);
		this.globleParameterRepository = applicationContext.getBean(GlobleParameterRepository.class);
		this.litechedulerService = applicationContext.getBean(LiteSchedulerService.class);

		TransactionTemplate coreTransactionTemplate = new TransactionTemplate(this.transactionManager);
		coreTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		coreTransactionTemplate.executeWithoutResult((state) -> {

			ScheduleRepository scheduleRepository = applicationContext.getBean(ScheduleRepository.class);
			JobGroupRepository jobGroupRepository = applicationContext.getBean(JobGroupRepository.class);
			JobRepository jobRepository = applicationContext.getBean(JobRepository.class);

			JobDataMap jobDataMap = context.getMergedJobDataMap();

			String scheduleId = (String) jobDataMap.get("scheduleId");

			ExecutionType executionType = (ExecutionType) jobDataMap.get("executionType");

			Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);

			ThreadContext.put("scheduleId", schedule.getId());

			List<ExecutionHistory> histories = schedule.getExecutionHistories();

			ExecutionHistory latestHistory = histories.stream().findFirst().orElse(null);

			if (latestHistory != null && latestHistory.getExecutionStatus() == ExecutionStatus.Running) {
				log.warn("Schedule is running, execution id = {}", latestHistory.getExecutionId());
				return;
			}

			log.info("------------Start Schedule {} ------------", schedule.getName());

			try {

				if (executionType == ExecutionType.Schedule) {
					List<JobGroup> jobGroupList = schedule.getJobGroups().stream().filter(jobGroup -> {
						return jobGroup.getState() == ScheduledState.Enabled;
					}).collect(Collectors.toList());
					for (JobGroup jobGroup : jobGroupList) {
						execute(jobGroup);
					}
				} else if (executionType == ExecutionType.Group) {
					Integer executionId = (Integer) jobDataMap.get("executionId");
					execute(jobGroupRepository.findById(executionId).orElse(null));
				} else if (executionType == ExecutionType.Job) {
					Integer executionId = (Integer) jobDataMap.get("executionId");
					execute(jobRepository.findById(executionId).orElse(null));
				}

			} catch (Exception e) {
				log.error("", e);
			} finally {
				litechedulerService.setExecutionType(schedule, ExecutionType.Schedule);
			}

			log.info("------------End Schedule {} ------------", schedule.getId());

			ThreadContext.clearAll();

		});

	}

	private void execute(JobGroup jobGroup) {
		log.info("------------Start JobGroup {} ------------", jobGroup.getName());

		List<lite.scheduler.core.entity.Job> jobList = jobGroup.getJobs().stream().filter(job -> {
			return job.getState() == ScheduledState.Enabled;
		}).collect(Collectors.toList());

		try {
			for (lite.scheduler.core.entity.Job job : jobList) {
				execute(job);
			}
		} catch (Exception e) {
			log.error("", e);
		}

		log.info("------------End JobGroup {} ------------", jobGroup.getName());
	}

	private void execute(lite.scheduler.core.entity.Job job) throws Exception {
		log.info("------------Start Job {} ------------", job.getName());

		MessageWriter messageWriter = new MessageWriter((line) -> log.info(line));
		ExecutionHistory executionHistory = new ExecutionHistory();
		try {
			String jobClassName = job.getClassName();
			Class<?> scheduleJobClass = Class.forName(jobClassName);
			ScheduleJob scheduleJob = (ScheduleJob) applicationContext.getBean(scheduleJobClass);

			List<GlobleParameter> globleParameters = globleParameterRepository.findAll();
			List<ScheduleParameter> scheduleParameters = job.getJobGroup().getSchedule().getScheduleParameters();
			List<JobGroupParameter> jobGroupParameter = job.getJobGroup().getJobGroupParameters();
			List<JobParameter> jobParameter = job.getJobParameters();
			ExecuteParamenter executeParamenter = new ExecuteParamenter(globleParameters, scheduleParameters, jobGroupParameter, jobParameter);

			executionHistory.setExecutionId(executionId);
			executionHistory.setStartDt(new Date());
			executionHistory.setJob(job);
			executionHistory.setJobGroup(job.getJobGroup());
			executionHistory.setSchedule(job.getJobGroup().getSchedule());
			executionHistory.setParameter(executeParamenter.toString());
			executionHistory.setMessage("");
			executionHistory.setExecutionStatus(ExecutionStatus.Running);
			saveExecutionHistory(executionHistory);

			messageWriter = new MessageWriter((line) -> {
				executionHistory.setMessage(executionHistory.getMessage() + line + "\n");
				saveExecutionHistory(executionHistory);
			});

			scheduleJob.internalExecute(executeParamenter, messageWriter);
			executionHistory.setEndDt(new Date());
			executionHistory.setExecutionStatus(ExecutionStatus.Succeeded);
			saveExecutionHistory(executionHistory);
		} catch (Exception e) {
			messageWriter.writeThrowable(e);
			executionHistory.setEndDt(new Date());
			executionHistory.setExecutionStatus(ExecutionStatus.Failed);
			saveExecutionHistory(executionHistory);
			throw e;
		}

		log.info("------------End Job {} ------------", job.getName());
	}

	private void saveExecutionHistory(ExecutionHistory executionHistory) {
		TransactionTemplate coreTransactionTemplate = new TransactionTemplate(this.transactionManager);
		coreTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		coreTransactionTemplate.executeWithoutResult(state -> {
			ExecutionHistoryRepository executionHistoryRepository = applicationContext.getBean(ExecutionHistoryRepository.class);
			executionHistoryRepository.save(executionHistory);
		});

	}

}
