package lite.scheduler.core.cmp;

import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

import lite.scheduler.core.CoreConfiguration;

public interface ScheduleJob {

	default void internalExecute(ExecuteParamenter executeParamenter, MessageWriter messageWriter) throws Exception {

		ApplicationContext applicationContext = CoreConfiguration.getApplicationContext();

		ModularTransactionManager modularTransactionManager = new ModularTransactionManager(applicationContext.getBeansOfType(PlatformTransactionManager.class), this);

		modularTransactionManager.execute(executeParamenter, messageWriter);
	}

	void execute(ExecuteParamenter executeParamenter, MessageWriter messageWriter) throws Exception;
}
