package lite.scheduler.core.cmp;

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

import lite.scheduler.core.CoreConfiguration;
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
import lite.scheduler.core.repo.ExecutionHistoryRepo;
import lite.scheduler.core.repo.GlobleParameterRepo;
import lite.scheduler.core.repo.JobGroupRepo;
import lite.scheduler.core.repo.JobRepo;
import lite.scheduler.core.repo.ScheduleRepo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final public class InternalScheduledJob implements Job {

	private ApplicationContext applicationContext;

	private SchedulerManipulator schedulerManipulator;
	private JpaTransactionManager transactionManager;

	GlobleParameterRepo globleParameterRepo;

	private String executionId;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		executionId = UUID.randomUUID().toString();

		this.applicationContext = CoreConfiguration.getApplicationContext();
		this.transactionManager = applicationContext.getBean("core_transactionManager", JpaTransactionManager.class);
		this.globleParameterRepo = applicationContext.getBean(GlobleParameterRepo.class);
		this.schedulerManipulator = applicationContext.getBean(SchedulerManipulator.class);

		TransactionTemplate coreTransactionTemplate = new TransactionTemplate(this.transactionManager);
		coreTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		coreTransactionTemplate.executeWithoutResult((state) -> {

			ScheduleRepo scheduleRepo = applicationContext.getBean(ScheduleRepo.class);
			JobGroupRepo jobGroupRepo = applicationContext.getBean(JobGroupRepo.class);
			JobRepo jobRepo = applicationContext.getBean(JobRepo.class);

			JobDataMap jobDataMap = context.getMergedJobDataMap();

			String scheduleId = (String) jobDataMap.get("scheduleId");

			ExecutionType executionType = (ExecutionType) jobDataMap.get("executionType");

			Schedule schedule = scheduleRepo.findById(scheduleId).orElse(null);

			ThreadContext.put("scheduleId", schedule.getId());

			List<ExecutionHistory> histories = schedule.getExecutionHistories();

			ExecutionHistory latestHistory = histories.stream().findFirst().orElse(null);

			if (latestHistory != null && latestHistory.getExecutionStatus() == ExecutionStatus.Running) {
				log.warn("Schedule is running, execution id = {}", latestHistory.getExecutionId());
				return;
			}

			log.info("Start schedule [{}][{}]", schedule.getId(), schedule.getName());

			Boolean manualExecution = jobDataMap.containsKey("manualExecution") ? jobDataMap.getBoolean("manualExecution") : false;

			try {

				if (executionType == ExecutionType.Schedule) {
					List<JobGroup> jobGroupList = schedule.getJobGroups().stream().filter(jobGroup -> {
						return jobGroup.getState() == ScheduledState.Enabled;
					}).collect(Collectors.toList());
					for (JobGroup jobGroup : jobGroupList) {
						execute(jobGroup, manualExecution);
					}
				} else if (executionType == ExecutionType.Group) {
					Integer executionId = (Integer) jobDataMap.get("executionId");
					execute(jobGroupRepo.findById(executionId).orElse(null), manualExecution);
				} else if (executionType == ExecutionType.Job) {
					Integer executionId = (Integer) jobDataMap.get("executionId");
					execute(jobRepo.findById(executionId).orElse(null));
				}

			} catch (Exception e) {
				log.error("", e);
			} finally {
				schedulerManipulator.setExecutionType(schedule, ExecutionType.Schedule);
				schedulerManipulator.setManualExecution(schedule, false);
			}

			log.info("End schedule [{}][{}]", schedule.getId(), schedule.getName());

			ThreadContext.clearAll();

		});

	}

	private void execute(JobGroup jobGroup, boolean manualExecution) {

		ExecutionHistory latestHistory = jobGroup.getExecutionHistories().stream().findFirst().orElse(null);

		if (latestHistory != null && latestHistory.getExecutionStatus() == ExecutionStatus.Failed && !manualExecution) {
			log.warn("The last job group [{}] execution failed, skip this time, execution id = {}", jobGroup.getName(), latestHistory.getExecutionId());
			return;
		}

		log.info("Scheduling job group [{}]", jobGroup.getName());

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

		log.info("Finished job group [{}]", jobGroup.getName());
	}

	private void execute(lite.scheduler.core.entity.Job job) throws Exception {
		log.info("Execute job [{}]", job.getName());

		MessageWriter messageWriter = new MessageWriter((line) -> log.info(line));
		ExecutionHistory executionHistory = new ExecutionHistory();
		try {
			String jobClassName = job.getClassName();
			Class<?> scheduleJobClass = Class.forName(jobClassName);
			ScheduleJob scheduleJob = (ScheduleJob) applicationContext.getBean(scheduleJobClass);

			List<GlobleParameter> globleParameters = globleParameterRepo.findAll();
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

		log.info("Complete job [{}]", job.getName());
	}

	private void saveExecutionHistory(ExecutionHistory executionHistory) {
		TransactionTemplate coreTransactionTemplate = new TransactionTemplate(this.transactionManager);
		coreTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		coreTransactionTemplate.executeWithoutResult(state -> {
			ExecutionHistoryRepo executionHistoryRepo = applicationContext.getBean(ExecutionHistoryRepo.class);
			executionHistoryRepo.save(executionHistory);
		});

	}

}
