package loggingframework.format;

import loggingframework.entity.LoggingMessage;

public class PlainMessageFormatter implements LogMessageFormatter {
    @Override
    public String format(LoggingMessage message) {
        StringBuilder builder = new StringBuilder();
        builder.append(message.timestamp()).
                append("-").
                append("[").append(message.level().name()).append("]")
                .append("-")
                .append(message.clazz().getName())
                .append("-").append("thread-").append(message.threadName())
                .append(": ").append(message.content());
        return builder.toString();
    }
}
