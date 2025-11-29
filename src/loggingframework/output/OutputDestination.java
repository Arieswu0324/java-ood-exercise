package loggingframework.output;

import loggingframework.entity.LoggingMessage;

public interface OutputDestination {
    void output(LoggingMessage message);

    void close();
}
