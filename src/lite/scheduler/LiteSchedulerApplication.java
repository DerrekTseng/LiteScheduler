package lite.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.event.EventListener;

import lite.scheduler.core.enums.ExecutionStatus;
import lite.scheduler.core.repository.ExecutionHistoryRepository;

@Configuration
@SpringBootApplication
@ImportResource("classpath:/applicationContext.xml")
public class LiteSchedulerApplication extends SpringBootServletInitializer {

	@Autowired
	ExecutionHistoryRepository executionHistoryRepository;

	public static final Logger logger = LoggerFactory.getLogger(LiteSchedulerApplication.class);

	public static void main(String[] args) {
		// For run in Eclipse
		System.setProperty("catalina.base", System.getProperty("user.dir"));
		SpringApplication.run(LiteSchedulerApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		// For deploy WAR file to tomcat
		return application.sources(LiteSchedulerApplication.class);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void afterStartup() {
		executionHistoryRepository.findAll().stream().filter(executionHistory -> {
			return executionHistory.getExecutionStatus() == ExecutionStatus.Running;
		}).forEach(executionHistory -> {
			executionHistory.setExecutionStatus(ExecutionStatus.Terminated);
			executionHistoryRepository.save(executionHistory);
		});
	}

}
