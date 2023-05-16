package lite.scheduler.cmp;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.ThreadContext;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lite.scheduler.LiteSchedulerApplication;
import lite.scheduler.entity.GlobleParameter;
import lite.scheduler.entity.Task;
import lite.scheduler.entity.TaskHistory;
import lite.scheduler.repo.GlobleParameterRepo;
import lite.scheduler.repo.TaskHistoryRepo;
import lite.scheduler.repo.TaskRepo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final public class InternalScheduleTask implements Job {

	private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		ApplicationContext applicationContext = LiteSchedulerApplication.getApplicationContext();
		JpaTransactionManager transactionManager = applicationContext.getBean("core_transactionManager", JpaTransactionManager.class);
		GlobleParameterRepo globleParameterRepo = applicationContext.getBean(GlobleParameterRepo.class);
		SchedulerManipulator schedulerManipulator = applicationContext.getBean(SchedulerManipulator.class);

		TransactionTemplate coreTransactionTemplate = new TransactionTemplate(transactionManager);

		TaskRepo taskRepo = applicationContext.getBean(TaskRepo.class);

		TaskHistoryRepo taskHistoryRepo = applicationContext.getBean(TaskHistoryRepo.class);

		coreTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		coreTransactionTemplate.executeWithoutResult((state) -> {

			JobDataMap jobDataMap = context.getMergedJobDataMap();

			Integer taskRowid = jobDataMap.getInt("taskRowid");

			Task task = taskRepo.findById(taskRowid).orElse(null);

			ThreadContext.put("taskId", task.getId());

			List<TaskHistory> histories = task.getHistories();

			TaskHistory latestHistory = histories.stream().findFirst().orElse(null);

			Boolean manualExecution = jobDataMap.containsKey("manualExecute") ? jobDataMap.getBoolean("manualExecute") : false;

			if (latestHistory != null && latestHistory.getResult() == ExecutionResult.Running) {
				log.warn("Task is running, ship this time.");
				return;
			} else if (latestHistory != null && latestHistory.getResult() == ExecutionResult.Failed) {
				if (!manualExecution) {
					log.warn("Last result was failed, ship this time.");
					return;
				}
			}

			log.info("Start task [{}][{}]", task.getId(), task.getName());

			List<GlobleParameter> globleParameters = globleParameterRepo.findAll();

			ExecuteParamenter executeParamenter = new ExecuteParamenter();
			executeParamenter.readGlobleParameters(globleParameters);
			executeParamenter.readTaskParameters(task.getParameters());

			TaskHistory taskHistory = new TaskHistory();

			MessageWriter messageWriter = new MessageWriter((line) -> {
				log.info(line);
				taskHistory.setMessage(taskHistory.getMessage() + line + "\n");
				saveHistory(taskHistory, transactionManager, taskHistoryRepo);
			});

			try {

				taskHistory.setTask(task);
				taskHistory.setMessage("");

				taskHistory.setParameter(mapper.writeValueAsString(executeParamenter));
				taskHistory.setResult(ExecutionResult.Running);
				taskHistory.setSdate(new Date());
				saveHistory(taskHistory, transactionManager, taskHistoryRepo);

				Class<?> taskClass = Class.forName(task.getTaskClass());
				ScheduleTask scheduleTask = (ScheduleTask) applicationContext.getBean(taskClass);
				scheduleTask.internalExecute(executeParamenter, messageWriter);
				taskHistory.setResult(ExecutionResult.Succeeded);
			} catch (Exception e) {
				messageWriter.writeLine(e.getMessage());
				taskHistory.setResult(ExecutionResult.Failed);
				log.error("", e);
			} finally {
				taskHistory.setEdate(new Date());
				saveHistory(taskHistory, transactionManager, taskHistoryRepo);
			}

			schedulerManipulator.setManualExecute(task, false);

			log.info("End task [{}][{}]", task.getId(), task.getName());

			ThreadContext.clearAll();

		});

	}

	private void saveHistory(TaskHistory taskHistory, JpaTransactionManager transactionManager, TaskHistoryRepo taskHistoryRepo) {
		TransactionTemplate coreTransactionTemplate = new TransactionTemplate(transactionManager);
		coreTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		coreTransactionTemplate.executeWithoutResult(state -> {
			taskHistoryRepo.save(taskHistory);
		});
	}

}
