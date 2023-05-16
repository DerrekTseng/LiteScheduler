package lite.scheduler.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class HistoryParameter {

	List<Parameter> globleParameter;

	List<Parameter> taskParameter;

}
