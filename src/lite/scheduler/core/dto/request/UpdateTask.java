package lite.scheduler.core.dto.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateTask {

	@NotNull
	Integer rowid;

	@NotEmpty
	String id;

	@NotEmpty
	String name;

	@NotEmpty
	String cronExp;

	@NotEmpty
	String taskClass;

	@NotNull
	String description;
}
