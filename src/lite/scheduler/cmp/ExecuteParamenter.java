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

	Map<String, String> globleParameter;
	Map<String, String> taskParameter;

	public void readGlobleParameters(List<GlobleParameter> globleParameters) {
		Map<String, String> _globleParameter = new HashMap<>();
		globleParameters.stream().forEach(p -> {
			String name = p.getName();
			String data = p.getData();
			_globleParameter.put(name, data);
		});
		this.globleParameter = Collections.unmodifiableMap(_globleParameter);
	}

	public void readTaskParameters(List<TaskParameter> taskParameters) {
		Map<String, String> _taskParameter = new HashMap<>();
		taskParameters.stream().forEach(p -> {
			String name = p.getName();
			String data = p.getData();
			_taskParameter.put(name, data);
		});
		this.taskParameter = Collections.unmodifiableMap(_taskParameter);
	}

}
