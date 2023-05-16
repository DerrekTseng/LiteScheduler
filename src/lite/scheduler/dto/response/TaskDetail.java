package lite.scheduler.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class TaskDetail {

	String id;

	String name;

	String cronExp;

	String taskClass;

	String description;

	List<Parameter> parameters;
}
