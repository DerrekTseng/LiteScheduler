package lite.scheduler.core.dto.request;

import javax.validation.constraints.NotBlank;

import lite.scheduler.core.enums.ScheduledState;
import lombok.Data;

@Data
public class UpdateScheduleEnable {

	@NotBlank
	String id;

	ScheduledState state;
}
