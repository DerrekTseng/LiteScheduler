package lite.scheduler.core.dto;

import lombok.Data;

@Data
public class ResponseBean<T> {

	boolean succeeded;
	
	T data;

	public static <T> ResponseBean<T> success(T data) {
		ResponseBean<T> rb = new ResponseBean<>();
		rb.succeeded = true;
		rb.data = data;
		return rb;
	}

	public static <T> ResponseBean<T> error(T data) {
		ResponseBean<T> rb = new ResponseBean<>();
		rb.succeeded = false;
		rb.data = data;
		return rb;
	}

}
