package lite.scheduler.core.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RequestID {
	
	@NotBlank
	String id;
}
