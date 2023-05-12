package lite.scheduler.core.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.core.cmp.SchedulerManipulator;
import lite.scheduler.core.dto.ResponseMessage;
import lite.scheduler.core.dto.request.InsertSchedule;
import lite.scheduler.core.dto.response.GridJobGroupRow;
import lite.scheduler.core.dto.response.GridJobRow;
import lite.scheduler.core.dto.response.GridScheduleRow;
import lite.scheduler.core.dto.response.JobDetail;
import lite.scheduler.core.dto.response.JobGroupDetail;
import lite.scheduler.core.dto.response.ScheduleDetail;
import lite.scheduler.core.entity.ExecutionHistory;
import lite.scheduler.core.entity.Job;
import lite.scheduler.core.entity.JobGroup;
import lite.scheduler.core.entity.Schedule;
import lite.scheduler.core.enums.ScheduledState;
import lite.scheduler.core.repo.JobGroupRepo;
import lite.scheduler.core.repo.JobRepo;
import lite.scheduler.core.repo.ScheduleRepo;

@Service
public class WebService {

	@Autowired
	SchedulerManipulator schedulerManipulator;

	@Autowired
	ScheduleRepo scheduleRepo;

	@Autowired
	JobGroupRepo jobGroupRepo;

	@Autowired
	JobRepo jobRepo;

	@Transactional
	public List<GridScheduleRow> getGridScheduleRows() {
		return scheduleRepo.findAll().stream().map(schedule -> {
			GridScheduleRow row = new GridScheduleRow();
			row.setId(schedule.getId());
			row.setName(schedule.getName());
			ExecutionHistory lastHistory = schedule.getExecutionHistories().stream().findFirst().orElse(null);
			if (lastHistory != null) {
				row.setLastEndDate(lastHistory.getEndDt());
				row.setLastExecutedState(lastHistory.getExecutionStatus());
			}
			row.setNextStartDate(schedulerManipulator.getNextFireDate(schedule));
			row.setState(schedule.getState());
			return row;
		}).collect(Collectors.toList());
	}

	@Transactional
	public ScheduleDetail getScheduleDetail(String id) {
		Schedule schedule = scheduleRepo.findById(id).orElse(null);
		ScheduleDetail scheduleDetail = new ScheduleDetail();
		scheduleDetail.setId(schedule.getId());
		scheduleDetail.setName(schedule.getName());
		scheduleDetail.setDescription(schedule.getDescription());

		String cronExps[] = schedule.getCronExp().split(" ");
		scheduleDetail.setMonth(parseCronExp(cronExps[5]));
		scheduleDetail.setDay(parseCronExp(cronExps[4]));
		scheduleDetail.setHour(parseCronExp(cronExps[2]));
		scheduleDetail.setMinute(parseCronExp(cronExps[1]));
		scheduleDetail.setSecond(parseCronExp(cronExps[0]));

		scheduleDetail.setScheduleParameters(schedule.getScheduleParameters());

		scheduleDetail.setGridJobGroupRows(schedule.getJobGroups().stream().map(jobGroup -> {
			GridJobGroupRow row = new GridJobGroupRow();
			row.setId(jobGroup.getId());
			row.setName(jobGroup.getName());
			row.setSequence(jobGroup.getSequence());
			row.setState(jobGroup.getState());
			return row;
		}).collect(Collectors.toList()));

		return scheduleDetail;
	}

	@Transactional
	public JobGroupDetail getJobGroupDetail(Integer id) {
		JobGroup jobGroup = jobGroupRepo.findById(id).orElse(null);

		JobGroupDetail jobGroupDetail = new JobGroupDetail();
		jobGroupDetail.setId(jobGroup.getId());
		jobGroupDetail.setName(jobGroup.getName());
		jobGroupDetail.setDescription(jobGroup.getDescription());
		jobGroupDetail.setParameters(jobGroup.getJobGroupParameters());
		jobGroupDetail.setJobs(jobGroup.getJobs().stream().map(job -> {
			GridJobRow row = new GridJobRow();
			row.setId(job.getId());
			row.setName(job.getName());
			row.setSequence(job.getSequence());
			row.setState(job.getState());
			return row;
		}).collect(Collectors.toList()));

		return jobGroupDetail;
	}

	@Transactional
	public JobDetail getJobDetail(Integer id) {
		Job job = jobRepo.findById(id).orElse(null);
		JobDetail jobDetail = new JobDetail();
		jobDetail.setId(job.getId());
		jobDetail.setName(job.getName());
		jobDetail.setDescription(job.getDescription());
		jobDetail.setClassName(job.getClassName());
		jobDetail.setParameters(job.getJobParameters());
		return jobDetail;
	}

	public ResponseMessage setScheduleEnable(String id, ScheduledState state) {
		Schedule schedule = scheduleRepo.findById(id).orElse(null);
		if (schedule == null) {
			return ResponseMessage.error("id not found");
		} else {
			schedule.setState(state);
			scheduleRepo.save(schedule);
			schedulerManipulator.updateSchedule(schedule);
			return ResponseMessage.success("Update successfully");
		}
	}

	public ResponseMessage doCreateScheduleSave(@Valid InsertSchedule insertSchedule) {

		if (scheduleRepo.existsById(insertSchedule.getId())) {
			return ResponseMessage.error("排程代號已存在");
		}

		Schedule schedule = new Schedule();

		schedule.setId(insertSchedule.getId());
		schedule.setName(insertSchedule.getName());
		schedule.setDescription(insertSchedule.getDescription());
		schedule.setState(ScheduledState.Disabled);
		schedule.setExecutionHistories(new ArrayList<>());
		schedule.setScheduleParameters(new ArrayList<>());
		schedule.setJobGroups(new ArrayList<>());

		String month = parseCronExp(insertSchedule.getMonth());
		String day = parseCronExp(insertSchedule.getDay());
		String hour = parseCronExp(insertSchedule.getHour());
		String minute = parseCronExp(insertSchedule.getMinute());
		String second = parseCronExp(insertSchedule.getSecond());
		schedule.setCronExp(String.format("%s %s %s ? %s %s *", second, minute, hour, day, month));

		scheduleRepo.save(schedule);
		schedulerManipulator.addSchedule(schedule);

		return ResponseMessage.success("新增排程成功");
	}

	private String parseCronExp(Integer num) {
		if (num == null || num == -1) {
			return "*";
		} else {
			return num.toString();
		}
	}

	private Integer parseCronExp(String exp) {
		if (exp == null || exp.equals("*")) {
			return -1;
		} else {
			return Integer.valueOf(exp);
		}
	}

}
