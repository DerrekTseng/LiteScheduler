package lite.scheduler.core.web;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LiteSchedulerService {

	@Autowired
	InternalSchedulerService internalSchedulerService;

	
	public void test() throws SchedulerException {
		log.info("Test");
	}

}
