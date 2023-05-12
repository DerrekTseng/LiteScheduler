package lite.scheduler.core.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class InsertSchedule {

	@NotBlank
	String id;

	@NotBlank
	String name;

	@NotBlank
	String description;

	@NotNull
	Integer month;

	@NotNull
	Integer day;

	@NotNull
	Integer hour;

	@NotNull
	Integer minute;

	@NotNull
	Integer second;

}
