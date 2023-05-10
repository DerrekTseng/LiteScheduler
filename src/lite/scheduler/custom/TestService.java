package lite.scheduler.custom;

import java.util.ArrayList;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lite.scheduler.core.entity.Job;
import lite.scheduler.core.entity.JobGroup;
import lite.scheduler.core.entity.Schedule;
import lite.scheduler.core.enums.ScheduledState;
import lite.scheduler.core.repository.ScheduleRepository;
import lite.scheduler.core.web.InternalSchedulerService;

@Service
public class TestService {

	@Autowired
	ScheduleRepository scheduleRepository;

	@Autowired
	InternalSchedulerService internalSchedulerService;

	public void registerSchedules() {
		internalSchedulerService.registerSchedules();
	}

	public void insertTestJob() {

		String scheduleId = "TestSchedule";

		if (!scheduleRepository.existsById(scheduleId)) {
			Schedule schedule = new Schedule();
			schedule.setId(scheduleId);
			schedule.setName("Test Test");
			schedule.setCronExp("");
			schedule.setDescription("TT");
			schedule.setJobGroups(new ArrayList<>());
			schedule.setState(ScheduledState.Disabled);

			JobGroup jobGroup = new JobGroup();
			jobGroup.setName("AAAA");
			jobGroup.setSequence(0);
			jobGroup.setDescription("");
			jobGroup.setState(ScheduledState.Enabled);
			jobGroup.setJobs(new ArrayList<>());
			jobGroup.setSchedule(schedule);
			schedule.getJobGroups().add(jobGroup);

			Job job = new Job();
			job.setName("AADAD");
			job.setClassName("lite.scheduler.custom.CustomExampleJob");
			job.setDescription("");
			job.setJobGroup(jobGroup);
			job.setSequence(0);
			job.setState(ScheduledState.Enabled);
			jobGroup.getJobs().add(job);

			scheduleRepository.save(schedule);

		}

	}

	private Schedule getSchedule() {
		String scheduleId = "TestSchedule";
		return scheduleRepository.findById(scheduleId).orElse(null);
	}

	public void fireSchedule() throws SchedulerException {
		internalSchedulerService.fire(getSchedule());
	}

	public void fireGroup() throws SchedulerException {
		internalSchedulerService.fire(getSchedule().getJobGroups().get(0));
	}

	public void fireJob() throws SchedulerException {
		internalSchedulerService.fire(getSchedule().getJobGroups().get(0).getJobs().get(0));
	}

}
