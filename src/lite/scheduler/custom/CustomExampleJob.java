package lite.scheduler.custom;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lite.scheduler.core.bean.MessageWriter;
import lite.scheduler.core.interfaces.ScheduleJob;
import lite.scheduler.core.vo.ExecuteParamenter;
import lite.scheduler.custom.entity.TestTable;
import lite.scheduler.custom.repository.TestTableRepo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomExampleJob implements ScheduleJob {

	@Autowired
	TestTableRepo testTableRepo;

	@Override
	public void execute(ExecuteParamenter executeParamenter, MessageWriter messageWriter) throws Exception {
		log.info("Hello~~~~~~~~~~~~~~~~~~{}", System.getProperty("lite.scheduler.work.dir"));

		TestTable testTable = new TestTable();
		testTable.setName(UUID.randomUUID().toString());
		testTable.setData(UUID.randomUUID().toString());

		testTableRepo.save(testTable);

		
		messageWriter.writeLine("1");
		messageWriter.writeLine("2");
		messageWriter.writeLine("3");
		messageWriter.writeLine("4");
		
		throw new Exception("test exception");
	}

}
