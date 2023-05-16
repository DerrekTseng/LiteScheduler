package lite.scheduler.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.entity.TaskHistory;

@Repository
@Transactional(transactionManager = "coreTransactionManager")
public interface TaskHistoryRepo extends JpaRepository<TaskHistory, Integer>{

}
