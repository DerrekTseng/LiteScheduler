package lite.scheduler.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.entity.Task;

@Transactional(transactionManager = "coreTransactionManager")
public interface TaskRepo extends JpaRepository<Task, Integer> {

}
