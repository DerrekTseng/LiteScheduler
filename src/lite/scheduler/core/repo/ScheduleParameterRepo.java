package lite.scheduler.core.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.core.entity.ScheduleParameter;

@Repository
@Transactional
public interface ScheduleParameterRepo  extends JpaRepository<ScheduleParameter, Integer> {

}
