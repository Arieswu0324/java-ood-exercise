package loggingframework.entity;

import loggingframework.enums.Level;

import java.time.LocalDateTime;

public record LoggingMessage(Class clazz, Level level,
                             String threadName,
                             LocalDateTime timestamp,
                             String content) {

}
