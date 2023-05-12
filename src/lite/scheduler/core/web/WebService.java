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
import lite.scheduler.core.dto.SaveScheduleDto;
import lite.scheduler.core.entity.ExecutionHistory;
import lite.scheduler.core.entity.Job;
import lite.scheduler.core.entity.JobGroup;
import lite.scheduler.core.entity.Schedule;
import lite.scheduler.core.enums.ScheduledState;
import lite.scheduler.core.repo.JobGroupRepo;
import lite.scheduler.core.repo.JobRepo;
import lite.scheduler.core.repo.ScheduleRepo;
import lite.scheduler.core.vo.GridJobGroupRow;
import lite.scheduler.core.vo.GridJobRow;
import lite.scheduler.core.vo.GridScheduleRow;
import lite.scheduler.core.vo.JobDetail;
import lite.scheduler.core.vo.JobGroupDetail;
import lite.scheduler.core.vo.ScheduleDetail;

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
		scheduleDetail.setCronExp(schedule.getCronExp());
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

	public ResponseMessage createScheduleSave(@Valid SaveScheduleDto saveScheduleDto) {

		if (scheduleRepo.existsById(saveScheduleDto.getId())) {
			return ResponseMessage.error("排程代號已存在");
		}

		Schedule schedule = new Schedule();

		schedule.setId(saveScheduleDto.getId());
		schedule.setName(saveScheduleDto.getName());
		schedule.setDescription(saveScheduleDto.getDescription());
		schedule.setState(ScheduledState.Disabled);
		schedule.setExecutionHistories(new ArrayList<>());
		schedule.setScheduleParameters(new ArrayList<>());
		schedule.setJobGroups(new ArrayList<>());

		String month = parseCronExp(saveScheduleDto.getMonth());
		String day = parseCronExp(saveScheduleDto.getDay());
		String hour = parseCronExp(saveScheduleDto.getHour());
		String minute = parseCronExp(saveScheduleDto.getMinute());
		String second = parseCronExp(saveScheduleDto.getSecond());
		schedule.setCronExp(String.format("%s %s %s ? %s %s *", second, minute, hour, day, month));

		scheduleRepo.save(schedule);
		schedulerManipulator.addSchedule(schedule);
		
		return ResponseMessage.success("新增排程成功");
	}
	
	private String parseCronExp(Integer num) {
		if (num == null || num == -1) {
			return  "*";
		} else {
			return num.toString();
		}
	}

}
