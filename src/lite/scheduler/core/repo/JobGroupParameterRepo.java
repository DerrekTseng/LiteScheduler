package lite.scheduler.core.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.core.entity.JobGroupParameter;

@Repository
@Transactional
public interface JobGroupParameterRepo extends JpaRepository<JobGroupParameter, Integer> {

}