package lite.scheduler.dto.response;

import java.util.Date;

import lite.scheduler.cmp.ExecutionResult;
import lombok.Data;

@Data
public class HistoryState {

	Integer rowid;

	String taskId;

	String taskName;

	Date sdate;

	Date edate;

	ExecutionResult result;

}
