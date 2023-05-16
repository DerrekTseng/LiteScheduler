package lite.scheduler.dto.response;

import java.util.Date;

import lite.scheduler.cmp.ExecutionResult;
import lombok.Data;

@Data
public class TaskState {

	Integer rowid;
	
	String id;
	
	String name;
	
	Date lastEndDate;
	
	ExecutionResult lastExecutedResult;
	
	Date nextStartDate;
	
	Boolean enable;

}
