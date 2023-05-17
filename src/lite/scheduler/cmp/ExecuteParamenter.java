package lite.scheduler.cmp;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lite.scheduler.entity.GlobleParameter;
import lite.scheduler.entity.TaskParameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteParamenter {

	Map<String, String> globleParameterMap;
	Map<String, String> taskParameterMap;

	void readGlobleParameters(List<GlobleParameter> globleParameters) {
		Map<String, String> _globleParameter = new HashMap<>();
		globleParameters.stream().forEach(p -> {
			String name = p.getName();
			String data = p.getData();
			_globleParameter.put(name, data);
		});
		this.globleParameterMap = Collections.unmodifiableMap(_globleParameter);
	}

	void readTaskParameters(List<TaskParameter> taskParameters) {
		Map<String, String> _taskParameter = new HashMap<>();
		taskParameters.stream().forEach(p -> {
			String name = p.getName();
			String data = p.getData();
			_taskParameter.put(name, data);
		});
		this.taskParameterMap = Collections.unmodifiableMap(_taskParameter);
	}

	public String getGlobleParamenter(String name) {
		return getGlobleParamenter(name, null);
	}

	public String getGlobleParamenter(String name, String defaultValue) {
		return globleParameterMap.getOrDefault(name, defaultValue);
	}

	public String getTaskParamenter(String name) {
		return getTaskParamenter(name, null);
	}

	public String getTaskParamenter(String name, String defaultValue) {
		return taskParameterMap.getOrDefault(name, defaultValue);
	}

}
