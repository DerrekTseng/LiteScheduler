package lite.scheduler.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.entity.Task;
import lite.scheduler.entity.TaskHistory;

@Transactional(transactionManager = "coreTransactionManager")
public interface TaskHistoryRepo extends JpaRepository<TaskHistory, Integer> {

	@Query("SELECT t FROM TaskHistory t WHERE t.task = :task ORDER BY sdate DESC")
	Page<TaskHistory> queryPageByTask(Task task, Pageable pageable);

}
