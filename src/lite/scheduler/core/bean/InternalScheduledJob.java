package lite.scheduler.core.bean;

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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import lite.scheduler.core.LiteSchedulerConfiguration;
import lite.scheduler.core.entity.GlobleParameter;
import lite.scheduler.core.entity.JobGroup;
import lite.scheduler.core.entity.JobGroupParameter;
import lite.scheduler.core.entity.JobParameter;
import lite.scheduler.core.entity.Schedule;
import lite.scheduler.core.entity.ScheduleParameter;
import lite.scheduler.core.enums.ExecutionType;
import lite.scheduler.core.enums.ScheduledState;
import lite.scheduler.core.interfaces.ScheduleJob;
import lite.scheduler.core.repository.GlobleParameterRepository;
import lite.scheduler.core.repository.JobGroupRepository;
import lite.scheduler.core.repository.JobRepository;
import lite.scheduler.core.repository.ScheduleRepository;
import lite.scheduler.core.web.InternalSchedulerService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final public class InternalScheduledJob implements Job {

	private ApplicationContext applicationContext;
	private GlobleParameterRepository globleParameterRepository;
	private ScheduleRepository scheduleRepository;
	private JobGroupRepository jobGroupRepository;
	private JobRepository jobRepository;
	private InternalSchedulerService internalSchedulerService;
	private JpaTransactionManager transactionManager;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		this.applicationContext = LiteSchedulerConfiguration.getApplicationContext();
		this.transactionManager = applicationContext.getBean("core_transactionManager", JpaTransactionManager.class);

		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setName(UUID.randomUUID().toString());
		definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus status = transactionManager.getTransaction(definition);

		this.globleParameterRepository = applicationContext.getBean(GlobleParameterRepository.class);
		this.scheduleRepository = applicationContext.getBean(ScheduleRepository.class);
		this.jobGroupRepository = applicationContext.getBean(JobGroupRepository.class);
		this.jobRepository = applicationContext.getBean(JobRepository.class);
		this.internalSchedulerService = applicationContext.getBean(InternalSchedulerService.class);

		JobDataMap jobDataMap = context.getMergedJobDataMap();

		String scheduleId = (String) jobDataMap.get("scheduleId");

		ExecutionType executionType = (ExecutionType) jobDataMap.get("executionType");

		Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);

		ThreadContext.put("scheduleId", schedule.getId());

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
			internalSchedulerService.resetExecutionType(schedule);
			transactionManager.commit(status);
		}

		log.info("------------End Schedule {} ------------", schedule.getId());

		ThreadContext.clearAll();

	}

	private void execute(JobGroup jobGroup) {
		log.info("------------Start JobGroup {} ------------", jobGroup.getName());

		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setName(UUID.randomUUID().toString());
		definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus status = transactionManager.getTransaction(definition);

		List<lite.scheduler.core.entity.Job> jobList = jobGroup.getJobs().stream().filter(job -> {
			return job.getState() == ScheduledState.Enabled;
		}).collect(Collectors.toList());

		try {
			for (lite.scheduler.core.entity.Job job : jobList) {
				execute(job);
			}
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
		}

		log.info("------------End JobGroup {} ------------", jobGroup.getName());
	}

	private void execute(lite.scheduler.core.entity.Job job) throws Exception {
		log.info("------------Start Job {} ------------", job.getName());

		String jobClassName = job.getClassName();
		ScheduleJob scheduleJob = (ScheduleJob) applicationContext.getBean(Class.forName(jobClassName));

		List<GlobleParameter> globleParameters = globleParameterRepository.findAll();
		List<ScheduleParameter> scheduleParameters = job.getJobGroup().getSchedule().getScheduleParameters();
		List<JobGroupParameter> jobGroupParameter = job.getJobGroup().getJobGroupParameters();
		List<JobParameter> jobParameter = job.getJobParameters();

		ExecuteParamenter executeParamenter = new ExecuteParamenter(globleParameters, scheduleParameters, jobGroupParameter, jobParameter);
		scheduleJob.execute(executeParamenter);

		log.info("------------End Job {} ------------", job.getName());
	}

}
