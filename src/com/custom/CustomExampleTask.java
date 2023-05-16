package com.custom;

import org.springframework.stereotype.Component;

import lite.scheduler.cmp.ExecuteParamenter;
import lite.scheduler.cmp.MessageWriter;
import lite.scheduler.cmp.ScheduleTask;
import lite.scheduler.cmp.TransactionManagers;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@TransactionManagers({ "mysql_transactionManager" })
public class CustomExampleTask implements ScheduleTask {

	@Override
	public void execute(ExecuteParamenter executeParamenter, MessageWriter messageWriter) throws Exception {
		log.info("Hello World");
	}

}
