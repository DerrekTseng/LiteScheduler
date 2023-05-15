package lite.scheduler.core.dto.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateParameter {

	@NotNull
	Integer rowid;

	@NotEmpty
	String name;

	@NotEmpty
	String data;

	@NotNull
	String description;

}
