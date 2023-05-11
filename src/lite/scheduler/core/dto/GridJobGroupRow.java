package lite.scheduler.core.dto;

import lite.scheduler.core.enums.ScheduledState;
import lombok.Data;

@Data
public class GridJobGroupRow {

	Integer id;

	String name;

	Integer sequence;

	ScheduledState state;

}
