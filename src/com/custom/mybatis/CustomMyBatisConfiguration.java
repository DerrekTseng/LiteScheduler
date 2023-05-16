package com.custom.mybatis;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import lite.scheduler.cmp.CustomPropertyStored;

@Configuration
@MapperScan(basePackages = "com.custom.mybatis.mapper", sqlSessionFactoryRef = "mysql_mybatis_java_SqlSessionFactory")
public class CustomMyBatisConfiguration {

	@Autowired
	CustomPropertyStored customPropertyStored;

	@Bean
	@Qualifier("mysql_mybatis_java_TransactionManager")
	DataSourceTransactionManager mysql_mybatis_java_TransactionManager() {
		DataSourceTransactionManager dm = new DataSourceTransactionManager();
		dm.setDataSource(mysql_mybatis_java_DataSource());
		return dm;
	}

	@Bean
	@Qualifier("mysql_mybatis_java_SqlSessionFactory")
	SqlSessionFactoryBean mysql_mybatis_java_SqlSessionFactory() throws IOException {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(mysql_mybatis_java_DataSource());
		sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
		Resource[] mapperLocations = new PathMatchingResourcePatternResolver().getResources("classpath:mybatis-mapper/*.xml, classpath:mybatis-mapper/**/*.xml");
		sqlSessionFactoryBean.setMapperLocations(mapperLocations);
		return sqlSessionFactoryBean;
	}

	@Bean
	@Qualifier("mysql_mybatis_java_SqlSessionTemplate")
	SqlSessionTemplate mysql_mybatis_java_SqlSessionTemplate() throws IOException, Exception {
		return new SqlSessionTemplate(mysql_mybatis_java_SqlSessionFactory().getObject());
	}

	@Bean
	@Qualifier("mysql_mybatis_java_DataSource")
	DataSource mysql_mybatis_java_DataSource() {
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
