package lite.scheduler.dto.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateParameter {

	@NotNull
	Integer taskRowid;

	@NotEmpty
	String name;

	@NotEmpty
	String data;

	@NotNull
	String description;

}
