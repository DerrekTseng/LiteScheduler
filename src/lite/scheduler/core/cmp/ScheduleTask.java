package lite.scheduler.core.cmp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

import lite.scheduler.core.CoreConfiguration;

public interface ScheduleTask {

	default void internalExecute(ExecuteParamenter parameter, MessageWriter messageWriter) throws Exception {

		ApplicationContext applicationContext = CoreConfiguration.getApplicationContext();

		TransactionManagers transactionManagers = this.getClass().getDeclaredAnnotation(TransactionManagers.class);

		Map<String, PlatformTransactionManager> platformTransactionManagers;

		if (transactionManagers == null) {
			platformTransactionManagers = applicationContext.getBeansOfType(PlatformTransactionManager.class);
		} else {
			List<String> transactionManagerIds = Arrays.asList(transactionManagers.value());
			platformTransactionManagers = new HashMap<>();
			applicationContext.getBeansOfType(PlatformTransactionManager.class).entrySet().stream().filter(entry -> {
				return transactionManagerIds.contains(entry.getKey());
			}).forEach(entry -> {
				platformTransactionManagers.put(entry.getKey(), entry.getValue());
			});
		}

		ModularTransactionManager modularTransactionManager = new ModularTransactionManager(platformTransactionManagers, this);

		modularTransactionManager.execute(parameter, messageWriter);
	}

	void execute(ExecuteParamenter parameter, MessageWriter messageWriter) throws Exception;
}
