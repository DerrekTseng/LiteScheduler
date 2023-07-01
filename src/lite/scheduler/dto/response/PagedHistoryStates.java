package lite.scheduler.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class PagedHistoryStates {

	Integer totalPages;

	Long totalSize;

	List<HistoryState> list;

}
