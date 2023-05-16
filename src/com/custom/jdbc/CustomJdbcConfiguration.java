package com.custom.jdbc;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import lite.scheduler.cmp.CustomPropertyStored;

@Configuration
public class CustomJdbcConfiguration {

	@Autowired
	CustomPropertyStored customPropertyStored;

	@Bean
	@Qualifier("mysql_jdbc_java_TransactionManager")
	DataSourceTransactionManager mysql_jdbc_java_TransactionManager() {
		DataSourceTransactionManager dm = new DataSourceTransactionManager();
		dm.setDataSource(mysql_jdbc_java_DataSource());
		return dm;
	}

	@Bean
	@Qualifier("mysql_jdbc_java_JdbcTemplate")
	JdbcTemplate mysql_jdbc_java_JdbcTemplate() {
		return new JdbcTemplate(mysql_jdbc_java_DataSource());
	}

	@Bean
	@Qualifier("mysql_jdbc_java_DataSource")
	DataSource mysql_jdbc_java_DataSource() {
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
