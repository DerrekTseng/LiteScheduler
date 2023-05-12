package lite.scheduler.core.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lite.scheduler.core.dto.ResponseMessage;
import lite.scheduler.core.dto.request.InsertSchedule;
import lite.scheduler.core.dto.request.UpdateScheduleEnable;

@Controller
@RequestMapping
public class WebUpdateController {

	@Autowired
	WebService service;

	@ResponseBody
	@PostMapping(value = "updateScheduleEnable", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseMessage updateScheduleEnable(@Valid @RequestBody UpdateScheduleEnable updateScheduleEnable) {
		return service.setScheduleEnable(updateScheduleEnable.getId(), updateScheduleEnable.getState());
	}
	
	@ResponseBody
	@PostMapping(value = "insertSchedule", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseMessage insertSchedule(@Valid @RequestBody InsertSchedule insertSchedule) {
		return service.doCreateScheduleSave(insertSchedule);
	}


}
