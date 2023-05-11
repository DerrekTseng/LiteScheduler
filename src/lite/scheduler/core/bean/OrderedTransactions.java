package lite.scheduler.core.bean;

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

import lite.scheduler.core.interfaces.ScheduleJob;
import lite.scheduler.core.vo.ExecuteParamenter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderedTransactions {
	AtomicInteger transactionIndex = new AtomicInteger(0);

	final List<Entry<String, PlatformTransactionManager>> transactionManagers;
	final ScheduleJob scheduleJob;

	public OrderedTransactions(Map<String, PlatformTransactionManager> transactionManagers, ScheduleJob scheduleJob) {
		this.transactionManagers = transactionManagers.entrySet().stream().collect(Collectors.toList());
		this.scheduleJob = scheduleJob;
	}

	public void execute(ExecuteParamenter executeParamenter, MessageWriter messageWriter) throws Exception {

		if (transactionManagers.size() > transactionIndex.get()) {
			int index = transactionIndex.getAndAdd(1);
			String name = transactionManagers.get(index).getKey();
			log.info("Create transaction: {}", name);
			PlatformTransactionManager transactionManager = transactionManagers.get(index).getValue();
			DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
			definition.setName(UUID.randomUUID().toString());
			definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus status = transactionManager.getTransaction(definition);
			try {
				execute(executeParamenter, messageWriter);
				log.info("Commit transaction: {}", name);
				transactionManager.commit(status);
			} catch (Exception e) {
				log.info("Rollback transaction: {}", name);
				transactionManager.rollback(status);
				throw e;
			}

		} else {
			scheduleJob.execute(executeParamenter, messageWriter);
		}
	}

}
