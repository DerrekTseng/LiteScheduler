package lite.scheduler.core.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.core.entity.GlobleParameter;

@Repository
@Transactional
public interface GlobleParameterRepo extends JpaRepository<GlobleParameter, Integer> {

}
