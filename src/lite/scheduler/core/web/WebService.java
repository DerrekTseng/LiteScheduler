package lite.scheduler.core.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lite.scheduler.core.cmp.ExecuteParamenter;
import lite.scheduler.core.cmp.SchedulerManipulator;
import lite.scheduler.core.dto.request.CreateParameter;
import lite.scheduler.core.dto.request.CreateTask;
import lite.scheduler.core.dto.request.UpdateParameter;
import lite.scheduler.core.dto.request.UpdateTask;
import lite.scheduler.core.dto.response.HistoryParameter;
import lite.scheduler.core.dto.response.HistoryState;
import lite.scheduler.core.dto.response.Parameter;
import lite.scheduler.core.dto.response.TaskDetail;
import lite.scheduler.core.dto.response.TaskState;
import lite.scheduler.core.entity.GlobleParameter;
import lite.scheduler.core.entity.Task;
import lite.scheduler.core.entity.TaskHistory;
import lite.scheduler.core.entity.TaskParameter;
import lite.scheduler.core.repo.GlobleParameterRepo;
import lite.scheduler.core.repo.TaskHistoryRepo;
import lite.scheduler.core.repo.TaskParameterRepo;
import lite.scheduler.core.repo.TaskRepo;

@Service
public class WebService {

	private static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	SchedulerManipulator schedulerManipulator;

	@Autowired
	TaskRepo taskRepo;

	@Autowired
	GlobleParameterRepo globleParameterRepo;

	@Autowired
	TaskParameterRepo taskParameterRepo;

	@Autowired
	TaskHistoryRepo taskHistoryRepo;

	@Transactional(transactionManager = "core_transactionManager")
	public List<TaskState> qryTaskStates() {
		return taskRepo.findAll().stream().map(task -> {
			TaskState taskState = new TaskState();
			taskState.setRowid(task.getRowid());
			taskState.setId(task.getId());
			taskState.setName(task.getName());
			taskState.setEnable(task.getEnabled());

			TaskHistory taskHistory = task.getHistories().stream().findFirst().orElse(null);
			if (taskHistory == null) {
				taskState.setLastEndDate(null);
				taskState.setLastExecutedResult(null);
			} else {
				taskState.setLastEndDate(taskHistory.getEdate());
				taskState.setLastExecutedResult(taskHistory.getResult());
			}

			taskState.setNextStartDate(schedulerManipulator.getNextFireDate(task));

			return taskState;
		}).collect(Collectors.toList());
	}

	@Transactional(transactionManager = "core_transactionManager")
	public String updateTaskEnable(Integer rowid, Boolean enable) {
		Task task = taskRepo.findById(rowid).orElse(null);
		if (task == null) {
			return "「任務排程」不存在";
		}
		task.setEnabled(enable);
		taskRepo.save(task);
		schedulerManipulator.updateTask(task);
		return null;
	}

	@Transactional(transactionManager = "core_transactionManager")
	public String deleteTask(Integer rowid) {
		Task task = taskRepo.findById(rowid).orElse(null);
		if (task == null) {
			return "「任務排程」不存在";
		}
		schedulerManipulator.removeTask(task);
		taskRepo.delete(task);
		return null;
	}

	public String runTask(Integer rowid) {
		Task task = taskRepo.findById(rowid).orElse(null);
		if (task == null) {
			return "「任務排程」不存在";
		}
		schedulerManipulator.fire(task);
		return null;
	}

	@Transactional(transactionManager = "core_transactionManager")
	public String createTask(CreateTask createTask) {
		if (taskRepo.findAll().stream().anyMatch(task -> {
			return task.getId().equals(createTask.getId()) || task.getName().equals(createTask.getName());
		})) {
			return "「任務代號」或「任務名稱」不允許重複";
		}

		Task task = new Task();
		task.setId(createTask.getId());
		task.setName(createTask.getName());
		task.setCronExp(createTask.getCronExp());
		task.setTaskClass(createTask.getTaskClass());
		task.setDescription(createTask.getDescription());
		task.setEnabled(false);
		taskRepo.save(task);
		schedulerManipulator.addTask(task);
		return null;
	}

	@Transactional(transactionManager = "core_transactionManager")
	public TaskDetail qryTaskDetail(Integer rowid) {
		Task task = taskRepo.findById(rowid).orElse(null);
		if (task == null) {
			return null;
		}
		TaskDetail taskDetail = new TaskDetail();
		taskDetail.setId(task.getId());
		taskDetail.setName(task.getName());
		taskDetail.setCronExp(task.getCronExp());
		taskDetail.setTaskClass(task.getTaskClass());
		taskDetail.setDescription(task.getDescription());

		taskDetail.setParameters(task.getParameters().stream().map(p -> {
			Parameter parameter = new Parameter();
			parameter.setRowid(p.getRowid());
			parameter.setName(p.getName());
			parameter.setData(p.getData());
			parameter.setDescription(p.getDescription());
			return parameter;
		}).collect(Collectors.toList()));

		return taskDetail;
	}

	@Transactional(transactionManager = "core_transactionManager")
	public String updateTask(UpdateTask updateTask) {
		Task task = taskRepo.findById(updateTask.getRowid()).orElse(null);
		if (task == null) {
			return "「任務排程」不存在";
		}

		if (taskRepo.findAll().stream().anyMatch(t -> {
			if (t.getRowid() == task.getRowid()) {
				return false;
			} else {
				return t.getName().equals(updateTask.getName());
			}
		})) {
			return "「任務名稱」不允許重複";
		}

		task.setName(updateTask.getName());
		task.setCronExp(updateTask.getCronExp());
		task.setDescription(updateTask.getDescription());
		task.setTaskClass(updateTask.getTaskClass());
		taskRepo.save(task);
		schedulerManipulator.updateTask(task);
		return null;
	}

	@Transactional(transactionManager = "core_transactionManager")
	public List<Parameter> qryGlobleParameter() {
		return globleParameterRepo.findAll().stream().map(p -> {
			Parameter parameter = new Parameter();
			parameter.setRowid(p.getRowid());
			parameter.setName(p.getName());
			parameter.setData(p.getData());
			parameter.setDescription(p.getDescription());
			return parameter;
		}).collect(Collectors.toList());
	}

	@Transactional(transactionManager = "core_transactionManager")
	public String createGlobleParameter(CreateParameter createParameter) {
		if (globleParameterRepo.findAll().stream().anyMatch(p -> {
			return p.getName().equals(createParameter.getName());
		})) {
			return "「參數名稱」不允許重複";
		}
		GlobleParameter globleParameter = new GlobleParameter();
		globleParameter.setName(createParameter.getName());
		globleParameter.setData(createParameter.getData());
		globleParameter.setDescription(createParameter.getDescription());
		globleParameterRepo.save(globleParameter);
		return null;
	}

	@Transactional(transactionManager = "core_transactionManager")
	public String updateGlobleParameter(UpdateParameter updateParameter) {
		GlobleParameter globleParameter = globleParameterRepo.findById(updateParameter.getRowid()).orElse(null);
		if (globleParameter == null) {
			return "「全域參數」不存在";
		}

		if (globleParameterRepo.findAll().stream().anyMatch(p -> {
			if (globleParameter.getRowid() == p.getRowid()) {
				return false;
			} else {
				return p.getName().equals(updateParameter.getName());
			}
		})) {
			return "「參數名稱」不允許重複";
		}

		globleParameter.setName(updateParameter.getName());
		globleParameter.setData(updateParameter.getData());
		globleParameter.setDescription(updateParameter.getDescription());
		globleParameterRepo.save(globleParameter);
		return null;
	}

	@Transactional(transactionManager = "core_transactionManager")
	public String deleteGlobleParameter(Integer rowid) {
		GlobleParameter globleParameter = globleParameterRepo.findById(rowid).orElse(null);
		if (globleParameter == null) {
			return "「全域參數」不存在";
		}
		globleParameterRepo.delete(globleParameter);
		return null;
	}

	@Transactional(transactionManager = "core_transactionManager")
	public String createTaskParameter(CreateParameter createParameter) {
		Task task = taskRepo.findById(createParameter.getTaskRowid()).orElse(null);
		if (task == null) {
			return "「任務排程」不存在";
		}

		if (task.getParameters().stream().anyMatch(p -> {
			return p.getName().equals(createParameter.getName());
		})) {
			return "「參數名稱」不允許重複";
		}

		TaskParameter taskParameter = new TaskParameter();
		taskParameter.setName(createParameter.getName());
		taskParameter.setData(createParameter.getData());
		taskParameter.setDescription(createParameter.getDescription());
		taskParameter.setTask(task);
		taskParameterRepo.save(taskParameter);
		return null;
	}

	@Transactional(transactionManager = "core_transactionManager")
	public String updateTaskParameter(UpdateParameter updateParameter) {

		TaskParameter taskParameter = taskParameterRepo.findById(updateParameter.getRowid()).orElse(null);

		if (taskParameter == null) {
			return "「排程參數」不存在";
		}

		if (taskParameter.getTask().getParameters().stream().anyMatch(p -> {
			if (updateParameter.getRowid() == p.getRowid()) {
				return false;
			} else {
				return p.getName().equals(updateParameter.getName());
			}

		})) {
			return "「參數名稱」不允許重複";
		}

		taskParameter.setName(updateParameter.getName());
		taskParameter.setData(updateParameter.getData());
		taskParameter.setDescription(updateParameter.getDescription());
		taskParameterRepo.save(taskParameter);
		return null;
	}

	@Transactional(transactionManager = "core_transactionManager")
	public String deleteTaskParameter(Integer rowid) {
		TaskParameter taskParameter = taskParameterRepo.findById(rowid).orElse(null);
		if (taskParameter == null) {
			return "「排程參數」不存在";
		}
		taskParameterRepo.delete(taskParameter);
		return null;
	}

	@Transactional(transactionManager = "core_transactionManager")
	public Parameter qryGlobleParameter(Integer rowid) {
		GlobleParameter globleParameter = globleParameterRepo.findById(rowid).orElse(null);
		if (globleParameter == null) {
			return null;
		}

		Parameter parameter = new Parameter();
		parameter.setRowid(globleParameter.getRowid());
		parameter.setName(globleParameter.getName());
		parameter.setData(globleParameter.getData());
		parameter.setDescription(globleParameter.getDescription());

		return parameter;
	}

	@Transactional(transactionManager = "core_transactionManager")
	public Parameter qryTaskParameter(Integer rowid) {
		TaskParameter taskParameter = taskParameterRepo.findById(rowid).orElse(null);
		if (taskParameter == null) {
			return null;
		}

		Parameter parameter = new Parameter();
		parameter.setRowid(taskParameter.getRowid());
		parameter.setName(taskParameter.getName());
		parameter.setData(taskParameter.getData());
		parameter.setDescription(taskParameter.getDescription());

		return parameter;
	}

	@Transactional(transactionManager = "core_transactionManager")
	public List<HistoryState> qryTaskHistoryStates(Integer rowid) {
		Task task = taskRepo.findById(rowid).orElse(null);
		if (task == null) {
			return null;
		}

		return task.getHistories().stream().map(h -> {
			HistoryState historyState = new HistoryState();
			historyState.setRowid(h.getRowid());
			historyState.setTaskId(task.getId());
			historyState.setTaskName(task.getName());
			historyState.setSdate(h.getSdate());
			historyState.setEdate(h.getEdate());
			historyState.setResult(h.getResult());
			return historyState;
		}).collect(Collectors.toList());
	}

	@Transactional(transactionManager = "core_transactionManager")
	public String qryTaskHistoryMessage(Integer rowid) {
		TaskHistory taskHistory = taskHistoryRepo.findById(rowid).orElse(null);
		if (taskHistory == null) {
			return null;
		}
		return taskHistory.getMessage();
	}

	@Transactional(transactionManager = "core_transactionManager")
	public HistoryParameter qryTaskHistoryParameter(Integer rowid) throws JsonMappingException, JsonProcessingException {
		TaskHistory taskHistory = taskHistoryRepo.findById(rowid).orElse(null);
		if (taskHistory == null) {
			return null;
		}

		String taskHistoryParameter = taskHistory.getParameter();
		
		ExecuteParamenter executeParamenter = mapper.readValue(taskHistoryParameter, ExecuteParamenter.class);

		HistoryParameter historyParameter = new HistoryParameter();

		historyParameter.setGlobleParameter(executeParamenter.getGlobleParameter().entrySet().stream().map(entry -> {
			Parameter parameter = new Parameter();
			parameter.setName(entry.getKey());
			parameter.setData(entry.getValue());
			return parameter;
		}).collect(Collectors.toList()));

		historyParameter.setTaskParameter(executeParamenter.getTaskParameter().entrySet().stream().map(entry -> {
			Parameter parameter = new Parameter();
			parameter.setName(entry.getKey());
			parameter.setData(entry.getValue());
			return parameter;
		}).collect(Collectors.toList()));

		return historyParameter;
	}

}
