package lite.scheduler.core.cmp;

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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.core.entity.Job;
import lite.scheduler.core.entity.JobGroup;
import lite.scheduler.core.entity.Schedule;
import lite.scheduler.core.enums.ExecutionType;
import lite.scheduler.core.enums.ScheduledState;
import lite.scheduler.core.repo.ScheduleRepo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchedulerManipulator {

	@Autowired
	@Qualifier("coreScheduler")
	private Scheduler scheduler;

	@Autowired
	ScheduleRepo scheduleRepo;

	private Trigger createTrigger(JobDetail jobDetail, String cornExp, ScheduledState state) {
		if (StringUtils.isEmpty(cornExp) || state == ScheduledState.Disabled) {
			return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(jobDetail.getKey().getName()).startAt(new Date(Long.MAX_VALUE)).withSchedule(SimpleScheduleBuilder.simpleSchedule()).build();
		} else {
			return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(jobDetail.getKey().getName()).withSchedule(CronScheduleBuilder.cronSchedule(cornExp)).build();
		}
	}

	@Transactional
	public void registerSchedules() {
		scheduleRepo.findAll().forEach(this::addSchedule);
	}

	public void addSchedule(Schedule schedule) {
		try {
			log.info("Register schedule [{}][{}]", schedule.getId(), schedule.getName());
			String id = schedule.getId();
			String cornExp = schedule.getCronExp();
			ScheduledState state = schedule.getState();
			JobDetail jobDetail = JobBuilder.newJob(InternalScheduledJob.class).withIdentity(id).storeDurably(true).build();
			Trigger trigger = createTrigger(jobDetail, cornExp, state);
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
			log.info("Update schedule [{}][{}]", schedule.getId(), schedule.getName());
			String cornExp = schedule.getCronExp();
			ScheduledState state = schedule.getState();
			TriggerKey triggerKey = new TriggerKey(schedule.getId());
			JobKey jobKey = new JobKey(schedule.getId());
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			Trigger trigger = createTrigger(jobDetail, cornExp, state);
			scheduler.rescheduleJob(triggerKey, trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void removeSchedule(Schedule schedule) {
		try {
			log.info("Remove schedule [{}][{}]", schedule.getId(), schedule.getName());
			JobKey jobKey = new JobKey(schedule.getId());
			scheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public Date getNextFireDate(Schedule schedule) {
		try {
			if (schedule.getState() == ScheduledState.Disabled) {
				return null;
			} else {
				TriggerKey triggerKey = new TriggerKey(schedule.getId());
				return scheduler.getTrigger(triggerKey).getNextFireTime();
			}
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void fire(Schedule schedule) {
		try {
			log.info("Fire schedule [{}][{}]", schedule.getId(), schedule.getName());
			String scheduleId = schedule.getId();
			JobKey jobKey = new JobKey(scheduleId);
			JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
			jobDataMap.put("executionType", ExecutionType.Schedule);
			jobDataMap.put("manualExecution", Boolean.TRUE);
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void fire(JobGroup jobGroup) {
		try {
			log.info("Fire group [{}][{}][{}]", jobGroup.getSchedule().getId(), jobGroup.getSchedule().getName(), jobGroup.getName());
			String scheduleId = jobGroup.getSchedule().getId();
			JobKey jobKey = new JobKey(scheduleId);
			JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
			jobDataMap.put("executionType", ExecutionType.Group);
			jobDataMap.put("executionId", jobGroup.getId());
			jobDataMap.put("manualExecution", Boolean.TRUE);
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void fire(Job job) {
		try {
			log.info("Fire group [{}][{}][{}][{}]", job.getJobGroup().getSchedule().getId(), job.getJobGroup().getSchedule().getName(), job.getJobGroup().getName(), job.getName());
			String scheduleId = job.getJobGroup().getSchedule().getId();
			JobKey jobKey = new JobKey(scheduleId);
			JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
			jobDataMap.put("executionType", ExecutionType.Job);
			jobDataMap.put("executionId", job.getId());
			jobDataMap.put("manualExecution", Boolean.TRUE);
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

	public void setManualExecution(Schedule schedule, Boolean b) {
		try {
			String scheduleId = schedule.getId();
			JobKey jobKey = new JobKey(scheduleId);
			JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
			jobDataMap.put("manualExecution", b);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
}
