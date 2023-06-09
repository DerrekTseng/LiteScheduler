package lite.scheduler.cmp;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModularTransactionManager {
	AtomicInteger transactionIndex = new AtomicInteger(0);

	final List<Entry<String, PlatformTransactionManager>> transactionManagers;
	final ScheduleTask scheduleTask;

	ModularTransactionManager(Map<String, PlatformTransactionManager> transactionManagers, ScheduleTask scheduleTask) {
		this.transactionManagers = transactionManagers.entrySet().stream().collect(Collectors.toList());
		this.scheduleTask = scheduleTask;
	}

	public void execute(ExecuteParamenter parameter, MessageWriter messageWriter) throws Exception {

		if (transactionManagers.size() > transactionIndex.get()) {
			int index = transactionIndex.getAndAdd(1);
			String name = transactionManagers.get(index).getKey();
			log.debug("Create transaction: {}", name);
			PlatformTransactionManager transactionManager = transactionManagers.get(index).getValue();
			DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
			definition.setName(UUID.randomUUID().toString());
			definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus status = transactionManager.getTransaction(definition);
			try {
				execute(parameter, messageWriter);
				log.debug("Commit transaction: {}", name);
				transactionManager.commit(status);
			} catch (Exception e) {
				log.debug("Rollback transaction: {}", name);
				transactionManager.rollback(status);
				throw e;
			}

		} else {
			scheduleTask.execute(parameter, messageWriter);
		}
	}

}
