package com.custom.jpa;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import lite.scheduler.cmp.CustomPropertyStored;

@Configuration
@EnableJpaRepositories(basePackages = "com.custom.jpa.repo", entityManagerFactoryRef = "mysql_jpa_java_EntityManagerFactory", transactionManagerRef = "mysql_jpa_java_TransactionManager")
public class CustomJpaConfiguration {

	@Autowired
	CustomPropertyStored customPropertyStored;

	@Bean
	@Qualifier("mysql_jpa_java_EntityManagerFactory")
	LocalContainerEntityManagerFactoryBean mysql_jpa_java_EntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(mysql_jpa_java_DataSource());
		em.setPackagesToScan("com.custom.jpa.entity");
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
		properties.setProperty("hibernate.format_sql", "false");
		properties.setProperty("hibernate.show_sql", "false");
		properties.setProperty("hibernate.hbm2ddl.auto", "update");
		em.setJpaProperties(properties);
		return em;
	}

	@Bean
	@Qualifier("mysql_jpa_java_TransactionManager")
	JpaTransactionManager mysql_jpa_java_TransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(mysql_jpa_java_EntityManagerFactory().getObject());
		return transactionManager;
	}

	@Bean
	@Qualifier("mysql_jpa_java_DataSource")
	DataSource mysql_jpa_java_DataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl(customPropertyStored.getValue("mysql.url"));
		dataSource.setUsername(customPropertyStored.getValue("mysql.username"));
		dataSource.setPassword(customPropertyStored.getValue("mysql.password"));
		dataSource.setMaxTotal(100);
		dataSource.setValidationQuery("SELECT 1 FROM DUAL");
		dataSource.setMaxConnLifetimeMillis(14400000);
		dataSource.setTimeBetweenEvictionRunsMillis(600000);
		dataSource.setRemoveAbandonedTimeout(60);
		dataSource.setMinEvictableIdleTimeMillis(600000);
		dataSource.setRemoveAbandonedOnBorrow(true);
		dataSource.setRemoveAbandonedOnMaintenance(true);
		return dataSource;
	}
}
