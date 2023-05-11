package lite.scheduler.core.interfaces;

import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

import lite.scheduler.core.LiteSchedulerConfiguration;
import lite.scheduler.core.bean.MessageWriter;
import lite.scheduler.core.bean.ModularTransactionManager;
import lite.scheduler.core.vo.ExecuteParamenter;

public interface ScheduleJob {

	default void internalExecute(ExecuteParamenter executeParamenter, MessageWriter messageWriter) throws Exception {

		ApplicationContext applicationContext = LiteSchedulerConfiguration.getApplicationContext();

		ModularTransactionManager modularTransactionManager = new ModularTransactionManager(applicationContext.getBeansOfType(PlatformTransactionManager.class), this);

		modularTransactionManager.execute(executeParamenter, messageWriter);
	}

	void execute(ExecuteParamenter executeParamenter, MessageWriter messageWriter) throws Exception;
}
