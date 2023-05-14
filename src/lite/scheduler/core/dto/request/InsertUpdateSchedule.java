package lite.scheduler.core.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class InsertUpdateSchedule {

	@NotBlank
	String id;

	@NotBlank
	String name;

	@NotBlank
	String description;

	@NotNull
	String cronExp;

}
