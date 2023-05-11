package lite.scheduler.core.dto;

import javax.validation.constraints.NotBlank;

import lite.scheduler.core.enums.ScheduledState;
import lombok.Data;

@Data
public class SetScheduleEnableDto {

	@NotBlank
	String id;

	ScheduledState state;
}
