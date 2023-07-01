package lite.scheduler.dto.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class QueryTaskHistoryStates {

	@NotNull
	Integer rowid;

	@NotNull
	Integer pageNum;

	@NotNull
	Integer pageSize;
}
