package com.custom;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.custom.jpa.repo.CustomExampleRepo;
import com.custom.mybatis.mapper.CustomExampleMapper;

import lite.scheduler.cmp.ExecuteParamenter;
import lite.scheduler.cmp.MessageWriter;
import lite.scheduler.cmp.ScheduleTask;
import lite.scheduler.cmp.TransactionManagers;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@TransactionManagers({ //
		"mysql_jdbc_java_TransactionManager", "mysql_jdbc_xml_TransactionManager", //
		"mysql_jpa_java_TransactionManager", "mysql_jpa_xml_TransactionManager", //
		"mysql_mybatis_java_TransactionManager", "mysql_mybatis_xml_TransactionManager" //
})
public class CustomExampleTask implements ScheduleTask {

	@Autowired
	CustomExampleMapper customExampleMapper;

	@Autowired
	CustomExampleRepo customExampleRepo;

	@Autowired
	@Qualifier("mysql_jdbc_java_JdbcTemplate")
	JdbcTemplate java_JdbcTemplate;

	@Autowired
	@Qualifier("mysql_jdbc_xml_JdbcTemplate")
	JdbcTemplate xml_JdbcTemplate;

	@Autowired
	@Qualifier("mysql_mybatis_java_SqlSessionTemplate")
	SqlSessionTemplate java_SqlSessionTemplate;

	@Autowired
	@Qualifier("mysql_mybatis_xml_SqlSessionTemplate")
	SqlSessionTemplate xml_SqlSessionTemplate;

	@Override
	public void execute(ExecuteParamenter executeParamenter, MessageWriter messageWriter) throws Exception {
		log.info("Hello World");
	}

}
