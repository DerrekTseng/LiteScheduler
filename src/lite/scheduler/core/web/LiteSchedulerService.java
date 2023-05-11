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
public class LiteSchedulerService {

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
		scheduleRepository.findAll().forEach(this::addSchedule);
	}

	public void addSchedule(Schedule schedule) {
		try {
			log.info("register schedule {}", schedule);
			String id = schedule.getId();
			String cornExp = schedule.getCronExp();
			ScheduledState state = schedule.getState();
			JobDetail jobDetail = JobBuilder.newJob(InternalScheduledJob.class).withIdentity(id).storeDurably(true).build();
			Trigger trigger = createTrigger(id, cornExp, state);
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.put("scheduleId", id);
			jobDataMap.put("executionType", ExecutionType.Schedule);
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void updateSchedule(Schedule schedule) {
		try {
			log.info("update schedule {}", schedule);
			String id = schedule.getId();
			String cornExp = schedule.getCronExp();
			ScheduledState state = schedule.getState();
			Trigger trigger = createTrigger(id, cornExp, state);
			TriggerKey triggerKey = new TriggerKey(id);
			scheduler.rescheduleJob(triggerKey, trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void removeSchedule(Schedule schedule) {
		try {
			JobKey jobKey = new JobKey(schedule.getId());
			scheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public Date getNextFireDate(Schedule schedule) {
		try {
			TriggerKey triggerKey = new TriggerKey(schedule.getId());
			return scheduler.getTrigger(triggerKey).getNextFireTime();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void fire(Schedule schedule) {
		try {
			String scheduleId = schedule.getId();
			JobKey jobKey = new JobKey(scheduleId);
			JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
			jobDataMap.put("executionType", ExecutionType.Schedule);
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void fire(JobGroup jobGroup) {
		try {
			String scheduleId = jobGroup.getSchedule().getId();
			JobKey jobKey = new JobKey(scheduleId);
			JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
			jobDataMap.put("executionType", ExecutionType.Group);
			jobDataMap.put("executionId", jobGroup.getId());
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void fire(lite.scheduler.core.entity.Job job) {
		try {
			String scheduleId = job.getJobGroup().getSchedule().getId();
			JobKey jobKey = new JobKey(scheduleId);
			JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
			jobDataMap.put("executionType", ExecutionType.Job);
			jobDataMap.put("executionId", job.getId());
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void setExecutionType(Schedule schedule, ExecutionType executionType) {
		try {
			String scheduleId = schedule.getId();
			JobKey jobKey = new JobKey(scheduleId);
			JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
			jobDataMap.put("executionType", executionType);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

}
