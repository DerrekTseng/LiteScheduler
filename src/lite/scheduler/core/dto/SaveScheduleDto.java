package lite.scheduler.core.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SaveScheduleDto {

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
