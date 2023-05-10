package lite.scheduler.core.bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lite.scheduler.core.entity.GlobleParameter;
import lite.scheduler.core.entity.JobGroupParameter;
import lite.scheduler.core.entity.JobParameter;
import lite.scheduler.core.entity.ScheduleParameter;
import lombok.Getter;

@Getter
public class ExecuteParamenter {

	private final Map<String, String> globleParameter;
	private final Map<String, String> scheduleParameter;
	private final Map<String, String> jobGroupParameter;
	private final Map<String, String> jobParameter;

	public ExecuteParamenter(List<GlobleParameter> globleParameters, List<ScheduleParameter> scheduleParameters, List<JobGroupParameter> jobGroupParameters, List<JobParameter> jobParameters) {

		Map<String, String> _globleParameter = new HashMap<>();
		globleParameters.stream().filter(param -> {
			return param.getEnabled();
		}).forEach(p -> {
			String name = p.getName();
			String data = p.getData();
			_globleParameter.put(name, data);
		});

		Map<String, String> _scheduleParameter = new HashMap<>();
		scheduleParameters.stream().filter(param -> {
			return param.getEnabled();
		}).forEach(p -> {
			String name = p.getName();
			String data = p.getData();
			_scheduleParameter.put(name, data);
		});

		Map<String, String> _jobGroupParameter = new HashMap<>();
		jobGroupParameters.stream().filter(param -> {
			return param.getEnabled();
		}).forEach(p -> {
			String name = p.getName();
			String data = p.getData();
			_jobGroupParameter.put(name, data);
		});

		Map<String, String> _jobParameter = new HashMap<>();
		jobParameters.stream().filter(param -> {
			return param.getEnabled();
		}).forEach(p -> {
			String name = p.getName();
			String data = p.getData();
			_jobParameter.put(name, data);
		});

		this.globleParameter = Collections.unmodifiableMap(_globleParameter);
		this.scheduleParameter = Collections.unmodifiableMap(_scheduleParameter);
		this.jobGroupParameter = Collections.unmodifiableMap(_jobGroupParameter);
		this.jobParameter = Collections.unmodifiableMap(_jobParameter);
	}

}
