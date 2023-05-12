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
import lite.scheduler.core.dto.response.GridScheduleRow;
import lite.scheduler.core.dto.response.ScheduleDetail;

@Controller
@RequestMapping
public class WebQueryController {

	@Autowired
	WebService service;

	@ResponseBody
	@PostMapping(value = "qryGridScheduleRows", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<GridScheduleRow> qryGridScheduleRows() {
		return service.getGridScheduleRows();
	}

	@ResponseBody
	@PostMapping(value = "qryScheduleDetail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ScheduleDetail qryScheduleDetail(@Valid @RequestBody RequestID requestID) {
		return service.getScheduleDetail(requestID.getId());
	}

}
