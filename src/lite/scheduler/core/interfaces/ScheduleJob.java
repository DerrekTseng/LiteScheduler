package lite.scheduler.core.interfaces;

import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

import lite.scheduler.core.LiteSchedulerConfiguration;
import lite.scheduler.core.bean.MessageWriter;
import lite.scheduler.core.bean.OrderedTransactions;
import lite.scheduler.core.vo.ExecuteParamenter;

public interface ScheduleJob {

	default void internalExecute(ExecuteParamenter executeParamenter, MessageWriter messageWriter) throws Exception {

		ApplicationContext applicationContext = LiteSchedulerConfiguration.getApplicationContext();

		OrderedTransactions orderedTransactions = new OrderedTransactions(applicationContext.getBeansOfType(PlatformTransactionManager.class), this);

		orderedTransactions.execute(executeParamenter, messageWriter);
	}

	void execute(ExecuteParamenter executeParamenter, MessageWriter messageWriter) throws Exception;
}
