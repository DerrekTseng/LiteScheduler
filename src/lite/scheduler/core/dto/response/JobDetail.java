package lite.scheduler.core.dto.response;

import java.util.List;

import lite.scheduler.core.entity.JobParameter;
import lombok.Data;

@Data
public class JobDetail {

	Integer id;

	String name;

	String description;

	String className;

	List<JobParameter> parameters;

}
