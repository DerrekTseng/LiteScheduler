package lite.scheduler.core.cmp;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lite.scheduler.core.entity.GlobleParameter;
import lite.scheduler.core.entity.TaskParameter;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ExecuteParamenter {

	private final Map<String, String> globleParameter;
	private final Map<String, String> taskParameter;

	ExecuteParamenter(List<GlobleParameter> globleParameters, List<TaskParameter> taskParameters) {

		Map<String, String> _globleParameter = new HashMap<>();
		globleParameters.stream().forEach(p -> {
			String name = p.getName();
			String data = p.getData();
			_globleParameter.put(name, data);
		});

		Map<String, String> _taskParameter = new HashMap<>();
		taskParameters.stream().forEach(p -> {
			String name = p.getName();
			String data = p.getData();
			_taskParameter.put(name, data);
		});

		this.globleParameter = Collections.unmodifiableMap(_globleParameter);
		this.taskParameter = Collections.unmodifiableMap(_taskParameter);
	}

}
