package lite.scheduler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import lite.scheduler.cmp.CustomPropertyStored;

@Configuration
@EnableJpaRepositories(basePackages = "lite.scheduler.repo", entityManagerFactoryRef = "coreEntityManagerFactory", transactionManagerRef = "coreTransactionManager")
public class LiteSchedulerConfiguration implements WebMvcConfigurer {

	@Autowired
	@Qualifier("customProperties")
	List<Resource[]> customProperties;

	@Autowired
	Environment env;

	@Bean
	ServletWebServerFactory servletWebServerFactory() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		String springApplicationName = env.getProperty("spring.application.name").trim();
		if (StringUtils.isNotEmpty(springApplicationName)) {
			factory.setContextPath("/" + springApplicationName);
		}
		return factory;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/static/resources/");
		registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/favicon.ico");
	}

	@Bean
	SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("/static/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCheckExistence(true);
		templateEngine.setTemplateResolver(templateResolver);
		return templateEngine;
	}

	@Bean
	ViewResolver viewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		resolver.setCharacterEncoding("UTF-8");
		return resolver;
	}

	@Bean
	StdSchedulerFactory stdSchedulerFactory() throws SchedulerException {
		Properties properties = new Properties();
		properties.setProperty("org.quartz.threadPool.threadCount", "8");
		StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory(properties);
		return stdSchedulerFactory;
	}

	@Bean(destroyMethod = "shutdown", initMethod = "start")
	Scheduler coreScheduler() throws SchedulerException {
		Scheduler scheduler = stdSchedulerFactory().getScheduler();
		return scheduler;
	}

	@Bean
	@Primary
	@Qualifier("coreDataSource")
	DataSource coreDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		String workDir = System.getProperties().getProperty("lite.scheduler.work.dir");
		String url = String.format("jdbc:h2:file:%s", new File(workDir, "lite-scheduler").getPath());
		dataSource.setUrl(url);
		dataSource.setUsername("sa");
		dataSource.setPassword("sa");
		return dataSource;
	}

	@Bean
	@Primary
	@Qualifier("coreEntityManagerFactory")
	LocalContainerEntityManagerFactoryBean coreEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(coreDataSource());
		em.setPackagesToScan("lite.scheduler.entity");
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		properties.setProperty("hibernate.format_sql", "false");
		properties.setProperty("hibernate.show_sql", "false");
		properties.setProperty("hibernate.hbm2ddl.auto", "update");
		em.setJpaProperties(properties);
		return em;
	}

	@Bean
	@Primary
	@Qualifier("coreTransactionManager")
	JpaTransactionManager coreTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(coreEntityManagerFactory().getObject());
		return transactionManager;
	}

	@Bean
	@Primary
	@Qualifier("customPropertyStored")
	CustomPropertyStored customPropertyStored() throws IOException {
		return new CustomPropertyStored(customProperties.toArray(new Resource[customProperties.size()]));
	}

	@Bean
	static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(ApplicationContext applicationContext) {
		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		pspc.setIgnoreResourceNotFound(true);
		@SuppressWarnings("unchecked")
		List<Resource[]> customProperties = (List<Resource[]>) applicationContext.getBean("customProperties");
		pspc.setLocations(customProperties.toArray(new Resource[customProperties.size()]));
		pspc.setFileEncoding("utf-8");
		return pspc;
	}

}
