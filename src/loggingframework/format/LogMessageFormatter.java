package loggingframework.format;

import loggingframework.entity.LoggingMessage;

public interface LogMessageFormatter {

    String format(LoggingMessage message);
}
