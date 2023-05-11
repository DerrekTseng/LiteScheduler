package lite.scheduler.core.vo;

import java.util.Date;

import lite.scheduler.core.enums.ExecutionStatus;
import lite.scheduler.core.enums.ScheduledState;
import lombok.Data;

@Data
public class GridScheduleRow {

	String id;

	String name;

	Date lastEndDate;

	ExecutionStatus lastExecutedState;

	Date nextStartDate;

	ScheduledState state;

}
