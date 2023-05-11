package lite.scheduler.core.cmp;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageWriter {

	private final Consumer<String> consumer;

	MessageWriter(Consumer<String> consumer) {
		this.consumer = consumer;
	}

	public void writeLine(String line) {
		log.info(line);
		consumer.accept(line);
	}

	public void writeThrowable(Throwable throwable) {
		if (throwable == null) {
			return;
		}

		StringBuilder message = new StringBuilder();

		message.append(throwable.toString());
		message.append(" : ");
		message.append(throwable.getLocalizedMessage()).append("\n");
		for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
			message.append("\tat " + stackTraceElement).append("\n");
		}

		writeLine(message.toString());
	}

}
