package lite.scheduler.cmp;

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

import lite.scheduler.entity.Task;
import lite.scheduler.repo.TaskRepo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchedulerManipulator {

	@Autowired
	@Qualifier("coreScheduler")
	private Scheduler scheduler;

	@Autowired
	TaskRepo taskRepo;

	private Trigger createTrigger(JobDetail jobDetail, String cornExp, Boolean enable) {
		if (StringUtils.isEmpty(cornExp) || !enable) {
			return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(jobDetail.getKey().getName()).startAt(new Date(Long.MAX_VALUE)).withSchedule(SimpleScheduleBuilder.simpleSchedule()).build();
		} else {
			return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(jobDetail.getKey().getName()).withSchedule(CronScheduleBuilder.cronSchedule(cornExp)).build();
		}
	}

	@Transactional(transactionManager = "coreTransactionManager")
	public void registerTasks() {
		taskRepo.findAll().forEach(this::addTask);
	}

	public void addTask(Task task) {
		try {
			log.info("Register task [{}][{}]", task.getId(), task.getName());
			Integer rowid = task.getRowid();
			String cornExp = task.getCronExp();
			JobDetail jobDetail = JobBuilder.newJob(InternalScheduleTask.class).withIdentity(rowid.toString()).storeDurably(true).build();
			Trigger trigger = createTrigger(jobDetail, cornExp, task.getEnabled());
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.put("taskRowid", rowid);
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void updateTask(Task task) {
		try {
			log.info("Update task [{}][{}]", task.getId(), task.getName());
			String cornExp = task.getCronExp();
			TriggerKey triggerKey = new TriggerKey(task.getRowid().toString());
			JobKey jobKey = new JobKey(task.getRowid().toString());
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			Trigger trigger = createTrigger(jobDetail, cornExp, task.getEnabled());
			scheduler.rescheduleJob(triggerKey, trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void removeTask(Task task) {
		try {
			log.info("Remove schedule [{}][{}]", task.getId(), task.getName());
			JobKey jobKey = new JobKey(task.getRowid().toString());
			scheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public Date getNextFireDate(Task task) {
		try {
			if (!task.getEnabled()) {
				return null;
			} else {
				TriggerKey triggerKey = new TriggerKey(task.getRowid().toString());
				return scheduler.getTrigger(triggerKey).getNextFireTime();
			}
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void fire(Task task) {
		try {
			log.info("Fire task [{}][{}]", task.getId(), task.getName());
			Integer rowid = task.getRowid();
			JobKey jobKey = new JobKey(rowid.toString());
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.put("manualExecute", Boolean.TRUE);
			scheduler.addJob(jobDetail, true, true);
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	void setManualExecute(Task task, Boolean b) {
		try {
			Integer rowid = task.getRowid();
			JobKey jobKey = new JobKey(rowid.toString());
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.put("manualExecute", b);
			scheduler.addJob(jobDetail, true, true);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
}
