package lite.scheduler.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.entity.TaskParameter;

@Transactional(transactionManager = "coreTransactionManager")
public interface TaskParameterRepo extends JpaRepository<TaskParameter, Integer>{

}
