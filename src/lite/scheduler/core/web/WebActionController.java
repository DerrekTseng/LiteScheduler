package lite.scheduler.core.web;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lite.scheduler.core.dto.RequestID;
import lite.scheduler.core.dto.ResponseMessage;
import lite.scheduler.core.dto.request.InsertUpdateSchedule;
import lite.scheduler.core.dto.request.UpdateScheduleEnable;
import lite.scheduler.core.dto.response.GridScheduleRow;
import lite.scheduler.core.dto.response.ScheduleDetail;

@Controller
@RequestMapping
public class WebActionController {

	@Autowired
	WebService service;

	@ResponseBody
	@PostMapping(value = "qryGridScheduleRows", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<GridScheduleRow> qryGridScheduleRows() {
		return service.getGridScheduleRows();
	}

	@ResponseBody
	@PostMapping(value = "updateScheduleEnable", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseMessage updateScheduleEnable(@Valid @RequestBody UpdateScheduleEnable updateScheduleEnable) {
		return service.setScheduleEnable(updateScheduleEnable.getId(), updateScheduleEnable.getState());
	}

	@ResponseBody
	@PostMapping(value = "qryScheduleDetail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ScheduleDetail qryScheduleDetail(@Valid @RequestBody RequestID requestID) {
		return service.getScheduleDetail(requestID.getId());
	}

	@ResponseBody
	@PostMapping(value = "insertSchedule", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseMessage insertUpdateSchedule(@Valid @RequestBody InsertUpdateSchedule insertUpdateSchedule) {
		return service.doCreateSchedule(insertUpdateSchedule);
	}
	
	@ResponseBody
	@PostMapping(value = "updateSchedule", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseMessage updateSchedule(@Valid @RequestBody InsertUpdateSchedule insertUpdateSchedule) {
		return service.doUpdateSchedule(insertUpdateSchedule);
	}

	@ResponseBody
	@PostMapping(value = "deleteSchedule", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseMessage deleteSchedule(@Valid @RequestBody RequestID requestID) {
		return service.deleteSchedule(requestID.getId());
	}

	@ResponseBody
	@PostMapping(value = "executeSchedule", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseMessage executeSchedule(@Valid @RequestBody RequestID requestID) {
		return service.executeSchedule(requestID.getId());
	}

	@ResponseBody
	@PostMapping(value = "executeJobGroup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseMessage executeJobGroup(@Valid @RequestBody RequestID requestID) {
		return service.executeJobGroup(requestID.getId());
	}

	@ResponseBody
	@PostMapping(value = "executeJob", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseMessage executeJob(@Valid @RequestBody RequestID requestID) {
		return service.executeJob(requestID.getId());
	}

}
