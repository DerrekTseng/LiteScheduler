package lite.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:/applicationContext.xml")
public class LiteSchedulerApplication extends SpringBootServletInitializer {

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

}
