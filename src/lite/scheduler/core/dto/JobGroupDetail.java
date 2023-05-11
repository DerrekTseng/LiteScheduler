package lite.scheduler.core.dto;

import java.util.List;

import lite.scheduler.core.entity.JobGroupParameter;
import lombok.Data;

@Data
public class JobGroupDetail {

	Integer id;

	String name;

	String description;
	
	List<JobGroupParameter> parameters;
	
	List<GridJobRow> jobs;
	
}
