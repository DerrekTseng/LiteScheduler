package lite.scheduler.dto.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class TaskEnable {

	@NotNull
	Integer rowid;
	
	@NotNull
	Boolean enable;
}
