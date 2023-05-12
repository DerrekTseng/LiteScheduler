package lite.scheduler.core.dto.response;

import java.util.List;

import lite.scheduler.core.entity.ScheduleParameter;
import lombok.Data;

@Data
public class ScheduleDetail {

	String id;

	String name;

	String description;

	Integer month;

	Integer day;

	Integer hour;

	Integer minute;

	Integer second;

	List<ScheduleParameter> scheduleParameters;

	List<GridJobGroupRow> gridJobGroupRows;

}
