package lite.scheduler.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.entity.GlobleParameter;

@Transactional(transactionManager = "coreTransactionManager")
public interface GlobleParameterRepo extends JpaRepository<GlobleParameter, Integer> {

}
