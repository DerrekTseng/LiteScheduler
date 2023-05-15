package lite.scheduler.custom;

import org.springframework.stereotype.Component;

import lite.scheduler.core.cmp.ExecuteParamenter;
import lite.scheduler.core.cmp.MessageWriter;
import lite.scheduler.core.cmp.ScheduleTask;
import lite.scheduler.core.cmp.TransactionManagers;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@TransactionManagers({ "mysql_transactionManager" })
public class CustomExampleTask implements ScheduleTask {

	@Override
	public void execute(ExecuteParamenter executeParamenter, MessageWriter messageWriter) throws Exception {
		log.info("Hello World");
	}

}
