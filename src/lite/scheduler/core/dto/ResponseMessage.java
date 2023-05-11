package lite.scheduler.core.dto;

import lombok.Data;

@Data
public class ResponseMessage {
	boolean succeeded;
	String message;

	public static ResponseMessage success(String message) {
		ResponseMessage rm = new ResponseMessage();
		rm.succeeded = true;
		rm.message = message;
		return rm;
	}

	public static ResponseMessage error(String message) {
		ResponseMessage rm = new ResponseMessage();
		rm.succeeded = false;
		rm.message = message;
		return rm;
	}
}
