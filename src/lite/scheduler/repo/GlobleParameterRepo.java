package lite.scheduler.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.entity.GlobleParameter;

@Repository
@Transactional(transactionManager = "core_transactionManager")
public interface GlobleParameterRepo extends JpaRepository<GlobleParameter, Integer> {

}
