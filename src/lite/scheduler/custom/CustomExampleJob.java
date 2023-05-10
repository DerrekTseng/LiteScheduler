package lite.scheduler.custom;

import org.springframework.stereotype.Component;

import lite.scheduler.core.bean.ExecuteParamenter;
import lite.scheduler.core.interfaces.ScheduleJob;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomExampleJob implements ScheduleJob {

	@Override
	public void execute(ExecuteParamenter executeParamenter) throws Exception {
		
		log.info("Hello~~~~~~~~~~~~~~~~~~{}", System.getProperty("lite.scheduler.work.dir"));
	}

}
