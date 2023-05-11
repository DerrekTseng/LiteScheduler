package lite.scheduler.custom;

import org.springframework.stereotype.Component;

import lite.scheduler.core.cmp.ExecuteParamenter;
import lite.scheduler.core.cmp.MessageWriter;
import lite.scheduler.core.cmp.ScheduleJob;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomExampleJob implements ScheduleJob {

	@Override
	public void execute(ExecuteParamenter executeParamenter, MessageWriter messageWriter) throws Exception {
		log.info("Hello World");
	}

}
