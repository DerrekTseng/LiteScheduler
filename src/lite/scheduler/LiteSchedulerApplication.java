package lite.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.event.EventListener;

import lite.scheduler.cmp.ExecutionResult;
import lite.scheduler.cmp.SchedulerManipulator;
import lite.scheduler.repo.TaskHistoryRepo;

@Configuration
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, ThymeleafAutoConfiguration.class, JdbcTemplateAutoConfiguration.class })
@ImportResource("classpath:/applicationContext.xml")
public class LiteSchedulerApplication extends SpringBootServletInitializer {

	private static ApplicationContext staticContext;

	@Autowired
	ApplicationContext context;

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
	 * Get SpringApplicationContext
	 * 
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		return staticContext;
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
		TaskHistoryRepo taskHistoryRepo = staticContext.getBean(TaskHistoryRepo.class);

		taskHistoryRepo.findAll().stream().filter(history -> {
			return history.getResult() == ExecutionResult.Running;
		}).forEach(history -> {
			history.setResult(ExecutionResult.Terminated);
			taskHistoryRepo.save(history);
		});

		SchedulerManipulator schedulerManipulator = staticContext.getBean(SchedulerManipulator.class);
		schedulerManipulator.registerTasks();
	}

}
