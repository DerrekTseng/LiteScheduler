package lite.scheduler.custom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lite.scheduler.custom.entity.TestTable;

@Repository
@Transactional
public interface TestTableRepo extends JpaRepository<TestTable, Integer> {

}
