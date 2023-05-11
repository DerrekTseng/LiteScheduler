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

import lite.scheduler.core.enums.ExecutionStatus;
import lite.scheduler.core.repository.ExecutionHistoryRepository;
import lite.scheduler.core.web.LiteSchedulerService;

@Configuration
public class LiteSchedulerConfiguration {

	private static ApplicationContext staticContext;

	@Autowired
	ApplicationContext context;

	@Autowired
	ExecutionHistoryRepository executionHistoryRepository;

	@Autowired
	@Qualifier("quartzSchedulerThreadSize")
	Integer quartzSchedulerThreadSize;

	@EventListener(ApplicationReadyEvent.class)
	void afterStartup() {
		staticContext = context;
		executionHistoryRepository.findAll().stream().filter(executionHistory -> {
			return executionHistory.getExecutionStatus() == ExecutionStatus.Running;
		}).forEach(executionHistory -> {
			executionHistory.setExecutionStatus(ExecutionStatus.Terminated);
			executionHistoryRepository.save(executionHistory);
		});

		LiteSchedulerService iiteSchedulerService = staticContext.getBean(LiteSchedulerService.class);
		iiteSchedulerService.registerSchedules();
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
