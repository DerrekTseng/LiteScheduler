package lite.scheduler.core.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.core.entity.TaskHistory;

@Repository
@Transactional(transactionManager = "core_transactionManager")
public interface TaskHistoryRepo extends JpaRepository<TaskHistory, Integer>{

}
