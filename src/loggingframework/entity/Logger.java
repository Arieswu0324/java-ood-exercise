package loggingframework.entity;

import loggingframework.enums.Level;
import loggingframework.output.OutputDestination;
import loggingframework.service.LogManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Logger {

    private final Class clazz;

    private volatile Level level = Level.DEBUG;


    private volatile List<OutputDestination> destinations = new CopyOnWriteArrayList<>();

    public Logger(Class clazz) {
        this.clazz = clazz;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setDestinations(List<OutputDestination> destinations) {
        //清空现有并添加新的，copy on write
        this.destinations = new CopyOnWriteArrayList<>(destinations != null ? destinations : Collections.emptyList());
    }

    public List<OutputDestination> getDestinations() {
        return Collections.unmodifiableList(destinations);
    }

    public void log(Level level, String message) {
        if (level.shouldLog(this.level)) {//大于设置的日志等级才输出
            LoggingMessage loggingMessage = new LoggingMessage(clazz, level, Thread.currentThread().getName(), LocalDateTime.now(), message);
            writeToDestinations(loggingMessage);
        }
    }

    private void writeToDestinations(LoggingMessage message) {
        LogManager.getINSTANCE().getProcessor().process(message, destinations);
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void error(String message) {
        log(Level.ERROR, message);
    }

    public void debug(String message) {
        log(Level.DEBUG, message);
    }

    public void fatal(String message) {
        log(Level.FATAL, message);
    }

    public void warn(String message) {
        log(Level.WARNING, message);
    }

    public Class getClazz() {
        return this.clazz;
    }

}
