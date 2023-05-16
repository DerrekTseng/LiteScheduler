package lite.scheduler.web;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lite.scheduler.dto.RequestRowid;
import lite.scheduler.dto.ResponseBean;
import lite.scheduler.dto.request.CreateParameter;
import lite.scheduler.dto.request.CreateTask;
import lite.scheduler.dto.request.TaskEnable;
import lite.scheduler.dto.request.UpdateParameter;
import lite.scheduler.dto.request.UpdateTask;
import lite.scheduler.dto.response.HistoryParameter;
import lite.scheduler.dto.response.HistoryState;
import lite.scheduler.dto.response.Parameter;
import lite.scheduler.dto.response.TaskDetail;
import lite.scheduler.dto.response.TaskState;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class WebActionController {

	@Autowired
	WebService webService;

	@PostMapping("qryTaskStates")
	public ResponseBean<List<TaskState>> qryTaskStates() {
		List<TaskState> taskStates = webService.qryTaskStates();
		return ResponseBean.success(taskStates);
	}

	@PostMapping("qryGlobleParameters")
	public ResponseBean<List<Parameter>> qryGlobleParameters() {
		List<Parameter> parameters = webService.qryGlobleParameter();
		return ResponseBean.success(parameters);
	}

	@PostMapping("createGlobleParameter")
	public ResponseBean<String> createGlobleParameter(@Valid @RequestBody CreateParameter createParameter) {
		String result = webService.createGlobleParameter(createParameter);
		if (StringUtils.isEmpty(result)) {
			return ResponseBean.success("新增成功");
		} else {
			return ResponseBean.error(result);
		}
	}

	@PostMapping("qryGlobleParameter")
	public ResponseBean<Parameter> qryGlobleParameter(@Valid @RequestBody RequestRowid requestRowid) {
		Parameter parameter = webService.qryGlobleParameter(requestRowid.getRowid());
		if (parameter != null) {
			return ResponseBean.success(parameter);
		} else {
			return ResponseBean.error(null);
		}
	}

	@PostMapping("updateGlobleParameter")
	public ResponseBean<String> updateGlobleParameter(@Valid @RequestBody UpdateParameter updateParameter) {
		String result = webService.updateGlobleParameter(updateParameter);
		if (StringUtils.isEmpty(result)) {
			return ResponseBean.success("更新成功");
		} else {
			return ResponseBean.error(result);
		}
	}

	@PostMapping("deleteGlobleParameter")
	public ResponseBean<String> deleteGlobleParameter(@Valid @RequestBody RequestRowid requestRowid) {
		String result = webService.deleteGlobleParameter(requestRowid.getRowid());
		if (StringUtils.isEmpty(result)) {
			return ResponseBean.success("刪除成功");
		} else {
			return ResponseBean.error(result);
		}
	}

	@PostMapping("updateTaskEnable")
	public ResponseBean<String> updateTaskEnable(@Valid @RequestBody TaskEnable taskEnable) {
		String result = webService.updateTaskEnable(taskEnable.getRowid(), taskEnable.getEnable());
		if (StringUtils.isEmpty(result)) {
			return ResponseBean.success("更新成功");
		} else {
			return ResponseBean.error(result);
		}
	}

	@PostMapping("deleteTask")
	public ResponseBean<String> deleteTask(@Valid @RequestBody RequestRowid requestRowid) {
		String result = webService.deleteTask(requestRowid.getRowid());
		if (StringUtils.isEmpty(result)) {
			return ResponseBean.success("刪除成功");
		} else {
			return ResponseBean.error(result);
		}
	}

	@PostMapping("runTask")
	public ResponseBean<String> runTask(@Valid @RequestBody RequestRowid requestRowid) {
		String result = webService.runTask(requestRowid.getRowid());
		if (StringUtils.isEmpty(result)) {
			return ResponseBean.success("執行成功");
		} else {
			return ResponseBean.error(result);
		}
	}

	@PostMapping("createTask")
	public ResponseBean<String> createTask(@Valid @RequestBody CreateTask createTask) {
		String result = webService.createTask(createTask);
		if (StringUtils.isEmpty(result)) {
			return ResponseBean.success("建立成功");
		} else {
			return ResponseBean.error(result);
		}
	}

	@PostMapping("qryTaskDetail")
	public ResponseBean<TaskDetail> qryTaskDetail(@Valid @RequestBody RequestRowid requestRowid) {
		TaskDetail taskDetail = webService.qryTaskDetail(requestRowid.getRowid());
		if (taskDetail == null) {
			return ResponseBean.error(null);
		} else {
			return ResponseBean.success(taskDetail);
		}
	}

	@PostMapping("updateTask")
	public ResponseBean<String> updateTask(@Valid @RequestBody UpdateTask updateTask) {
		String result = webService.updateTask(updateTask);
		if (StringUtils.isEmpty(result)) {
			return ResponseBean.success("更新成功");
		} else {
			return ResponseBean.error(result);
		}
	}

	@PostMapping("deleteTaskParameter")
	public ResponseBean<String> deleteTaskParameter(@Valid @RequestBody RequestRowid requestRowid) {
		String result = webService.deleteTaskParameter(requestRowid.getRowid());
		if (StringUtils.isEmpty(result)) {
			return ResponseBean.success("刪除成功");
		} else {
			return ResponseBean.error(result);
		}
	}

	@PostMapping("createTaskParameter")
	public ResponseBean<String> createTaskParameter(@Valid @RequestBody CreateParameter createParameter) {
		String result = webService.createTaskParameter(createParameter);
		if (StringUtils.isEmpty(result)) {
			return ResponseBean.success("新增成功");
		} else {
			return ResponseBean.error(result);
		}
	}

	@PostMapping("qryTaskParameter")
	public ResponseBean<Parameter> qryTaskParameter(@Valid @RequestBody RequestRowid requestRowid) {
		Parameter parameter = webService.qryTaskParameter(requestRowid.getRowid());
		if (parameter != null) {
			return ResponseBean.success(parameter);
		} else {
			return ResponseBean.error(null);
		}
	}

	@PostMapping("updateTaskParameter")
	public ResponseBean<String> updateTaskParameter(@Valid @RequestBody UpdateParameter updateParameter) {
		String result = webService.updateTaskParameter(updateParameter);
		if (StringUtils.isEmpty(result)) {
			return ResponseBean.success("更新成功");
		} else {
			return ResponseBean.error(result);
		}
	}

	@PostMapping("qryTaskHistoryStates")
	public ResponseBean<List<HistoryState>> qryTaskHistoryStates(@Valid @RequestBody RequestRowid requestRowid) {
		List<HistoryState> result = webService.qryTaskHistoryStates(requestRowid.getRowid());
		if (result != null) {
			return ResponseBean.success(result);
		} else {
			return ResponseBean.error(null);
		}
	}

	@PostMapping("qryTaskHistoryMessage")
	public ResponseBean<String> qryTaskHistoryMessage(@Valid @RequestBody RequestRowid requestRowid) {
		String result = webService.qryTaskHistoryMessage(requestRowid.getRowid());
		if (result != null) {
			return ResponseBean.success(result);
		} else {
			return ResponseBean.error(null);
		}
	}

	@PostMapping("qryTaskHistoryParameter")
	public ResponseBean<HistoryParameter> qryTaskHistoryParameter(@Valid @RequestBody RequestRowid requestRowid) throws JsonMappingException, JsonProcessingException {
		HistoryParameter result = webService.qryTaskHistoryParameter(requestRowid.getRowid());
		if (result != null) {
			return ResponseBean.success(result);
		} else {
			return ResponseBean.error(null);
		}
	}

}
