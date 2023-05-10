package lite.scheduler.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.core.entity.ExecutionHistory;

@Repository
@Transactional
public interface ExecutionHistoryRepository extends JpaRepository<ExecutionHistory, Integer> {

}
