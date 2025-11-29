package loggingframework.output;

import loggingframework.entity.LoggingMessage;
import loggingframework.format.LogMessageFormatter;

public class ConsoleOutput implements OutputDestination {

    private final LogMessageFormatter messageFormatter;

    public ConsoleOutput(LogMessageFormatter formatter) {
        this.messageFormatter = formatter;
    }

    @Override
    public void output(LoggingMessage message) {
        String formatted = messageFormatter.format(message);
        System.out.println(formatted);
    }

    @Override
    public void close() {

    }
}
