package lite.scheduler.core.dto;

import java.util.Date;

import lite.scheduler.core.enums.ExecutionStatus;
import lombok.Data;

@Data
public class GridSchedulerRow {

	String id;
	
	String name;
	
	Date lastEndDate;
	
	ExecutionStatus lastExecutedState;
	
	Date nextStartDate;
	
}
