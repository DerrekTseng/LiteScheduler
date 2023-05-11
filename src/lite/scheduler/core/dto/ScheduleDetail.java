package lite.scheduler.core.dto;

import java.util.List;

import lite.scheduler.core.entity.ScheduleParameter;
import lombok.Data;

@Data
public class ScheduleDetail {

	String id;

	String name;

	String description;

	String cronExp;

	List<ScheduleParameter> scheduleParameters;
	
	List<GridJobGroupRow> gridJobGroupRows;
	
}
