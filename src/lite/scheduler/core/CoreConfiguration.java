package lite.scheduler.core;

import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lite.scheduler.core.cmp.ExecutionResult;
import lite.scheduler.core.cmp.SchedulerManipulator;
import lite.scheduler.core.repo.TaskHistoryRepo;

@Configuration
public class CoreConfiguration implements WebMvcConfigurer {

	private static ApplicationContext staticContext;

	@Autowired
	ApplicationContext context;

	@Autowired
	TaskHistoryRepo taskHistoryRepo;

	@Autowired
	@Qualifier("quartzSchedulerThreadSize")
	Integer quartzSchedulerThreadSize;

	@EventListener(ApplicationReadyEvent.class)
	void afterStartup() {
		staticContext = context;
		taskHistoryRepo.findAll().stream().filter(history -> {
			return history.getResult() == ExecutionResult.Running;
		}).forEach(history -> {
			history.setResult(ExecutionResult.Terminated);
			taskHistoryRepo.save(history);
		});

		SchedulerManipulator schedulerManipulator = staticContext.getBean(SchedulerManipulator.class);
		schedulerManipulator.registerTasks();
	}

	@Bean
	@Scope("singleton")
	StdSchedulerFactory stdSchedulerFactory() throws SchedulerException {
		Properties properties = new Properties();
		properties.setProperty("org.quartz.threadPool.threadCount", quartzSchedulerThreadSize.toString());
		StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory(properties);
		return stdSchedulerFactory;
	}

	@Bean(name = "coreScheduler", destroyMethod = "shutdown", initMethod = "start")
	@Scope("singleton")
	Scheduler scheduler() throws SchedulerException {
		Scheduler scheduler = stdSchedulerFactory().getScheduler();
		return scheduler;
	}

	public static ApplicationContext getApplicationContext() {
		return staticContext;
	}

}
