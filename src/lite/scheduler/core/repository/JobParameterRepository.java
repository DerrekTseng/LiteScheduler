package lite.scheduler.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.core.entity.JobParameter;

@Repository
@Transactional
public interface JobParameterRepository  extends JpaRepository<JobParameter, Integer> {

}
