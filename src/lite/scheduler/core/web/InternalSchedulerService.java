package lite.scheduler.core.web;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lite.scheduler.core.bean.InternalScheduledJob;
import lite.scheduler.core.entity.JobGroup;
import lite.scheduler.core.entity.Schedule;
import lite.scheduler.core.enums.ExecutionType;
import lite.scheduler.core.enums.ScheduledState;
import lite.scheduler.core.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InternalSchedulerService {

	@Autowired
	@Qualifier("coreScheduler")
	private Scheduler scheduler;

	@Autowired
	ScheduleRepository scheduleRepository;

	private Trigger createTrigger(String id, String cornExp, ScheduledState state) {
		if (StringUtils.isEmpty(cornExp) || state == ScheduledState.Disabled) {
			return TriggerBuilder.newTrigger().withIdentity(id).withSchedule(SimpleScheduleBuilder.simpleSchedule()).build();
		} else {
			return TriggerBuilder.newTrigger().withIdentity(id).withSchedule(CronScheduleBuilder.cronSchedule(cornExp)).build();
		}
	}

	public void registerSchedules() {
		scheduleRepository.findAll().forEach(schedule -> {
			try {
				addSchedule(schedule);
			} catch (SchedulerException e) {
				log.error("", e);
			}
		});
	}

	public void addSchedule(Schedule schedule) throws SchedulerException {
		
		log.info("register schedule {}", schedule.getName());
		
		String id = schedule.getId();
		String cornExp = schedule.getCronExp();
		ScheduledState state = schedule.getState();
		JobDetail jobDetail = JobBuilder.newJob(InternalScheduledJob.class).withIdentity(id).storeDurably(true).build();
		Trigger trigger = createTrigger(id, cornExp, state);
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		jobDataMap.put("scheduleId", id);
		jobDataMap.put("executionType", ExecutionType.Schedule);
		scheduler.scheduleJob(jobDetail, trigger);
	}

	public void updateSchedule(Schedule schedule) throws SchedulerException {
		String id = schedule.getId();
		String cornExp = schedule.getCronExp();
		ScheduledState state = schedule.getState();
		Trigger trigger = createTrigger(id, cornExp, state);
		TriggerKey triggerKey = new TriggerKey(id);
		scheduler.rescheduleJob(triggerKey, trigger);
	}

	public void removeSchedule(Schedule schedule) throws SchedulerException {
		JobKey jobKey = new JobKey(schedule.getId());
		scheduler.deleteJob(jobKey);
	}

	public Date getNextFireDate(Schedule schedule) throws SchedulerException {
		TriggerKey triggerKey = new TriggerKey(schedule.getId());
		return scheduler.getTrigger(triggerKey).getNextFireTime();
	}

	public void fire(Schedule schedule) throws SchedulerException {
		String scheduleId = schedule.getId();
		JobKey jobKey = new JobKey(scheduleId);
		JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
		jobDataMap.put("executionType", ExecutionType.Schedule);
		scheduler.triggerJob(jobKey);
	}

	public void fire(JobGroup jobGroup) throws SchedulerException {
		String scheduleId = jobGroup.getSchedule().getId();
		JobKey jobKey = new JobKey(scheduleId);
		JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
		jobDataMap.put("executionType", ExecutionType.Group);
		jobDataMap.put("executionId", jobGroup.getId());
		scheduler.triggerJob(jobKey);
	}

	public void fire(lite.scheduler.core.entity.Job job) throws SchedulerException {
		String scheduleId = job.getJobGroup().getSchedule().getId();
		JobKey jobKey = new JobKey(scheduleId);
		JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
		jobDataMap.put("executionType", ExecutionType.Job);
		jobDataMap.put("executionId", job.getId());
		scheduler.triggerJob(jobKey);
	}

	public void resetExecutionType(Schedule schedule) {
		try {
			String scheduleId = schedule.getId();
			JobKey jobKey = new JobKey(scheduleId);
			JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
			jobDataMap.put("executionType", ExecutionType.Schedule);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}

	}

}
