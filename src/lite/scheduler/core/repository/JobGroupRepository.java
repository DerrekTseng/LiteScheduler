package lite.scheduler.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.core.entity.JobGroup;

@Repository
@Transactional
public interface JobGroupRepository  extends JpaRepository<JobGroup, Integer> {

}