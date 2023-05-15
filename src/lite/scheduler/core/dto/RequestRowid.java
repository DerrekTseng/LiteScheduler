package lite.scheduler.core.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RequestRowid {

	@NotNull
	Integer rowid;
}
