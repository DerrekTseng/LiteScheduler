package lite.scheduler;

import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lite.scheduler.cmp.ExecutionResult;
import lite.scheduler.cmp.SchedulerManipulator;
import lite.scheduler.repo.TaskHistoryRepo;

@Configuration
@SpringBootApplication
@ImportResource("classpath:/applicationContext.xml")
public class LiteSchedulerApplication extends SpringBootServletInitializer implements WebMvcConfigurer {

	private static ApplicationContext staticContext;

	@Autowired
	ApplicationContext context;

	@Autowired
	TaskHistoryRepo taskHistoryRepo;

	@Autowired
	@Qualifier("quartzSchedulerThreadSize")
	Integer quartzSchedulerThreadSize;

	/**
	 * For run in Eclipse
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("lite.scheduler.work.dir", System.getProperty("user.dir"));
		SpringApplication.run(LiteSchedulerApplication.class, args);
	}

	/**
	 * For deploy WAR file to Tomcat
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		System.setProperty("lite.scheduler.work.dir", System.getProperty("catalina.base"));
		return application.sources(LiteSchedulerApplication.class);
	}

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
