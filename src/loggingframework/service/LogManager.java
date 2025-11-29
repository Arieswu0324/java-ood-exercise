package loggingframework.service;

import loggingframework.entity.Logger;
import loggingframework.entity.LoggingConfiguration;
import loggingframework.enums.Level;
import loggingframework.output.OutputDestination;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LogManager {

    private static final LogManager INSTANCE = new LogManager();

    private LoggingConfiguration config = new LoggingConfiguration();

    private final Map<Class, Logger> loggerMap = new ConcurrentHashMap<>();

    private final ReentrantLock lock = new ReentrantLock();

    private final AsyncOutputProcessor processor = new AsyncOutputProcessor();

    public static LogManager getINSTANCE() {
        return INSTANCE;
    }

    public AsyncOutputProcessor getProcessor() {
        return processor;
    }

    public void updateLevel(Level newLevel) {
        lock.lock();
        try {
            config.setLevel(newLevel);
            applyToLoggers(config);
        } finally {
            lock.unlock();
        }
    }

    public void updateDestination(List<OutputDestination> destinationList) {
        lock.lock();
        try {
            config.setDestinations(destinationList);
            applyToLoggers(config);
        } finally {
            lock.unlock();
        }
    }


    public void updateConfig(LoggingConfiguration newConfig) {
        lock.lock();
        try {
            //创建了一个深拷贝
            LoggingConfiguration copy = new LoggingConfiguration();
            copy.setLevel(newConfig.getLevel());
            copy.setDestinations(newConfig.getDestinations());
            this.config = copy;
            applyToLoggers(config);
        } finally {
            lock.unlock();
        }
    }

    public void addToManager(Logger logger) {
        lock.lock();
        try {
            logger.setLevel(config.getLevel());
            logger.setDestinations(config.getDestinations());
            loggerMap.put(logger.getClazz(), logger);
        } finally {
            lock.unlock();
        }
    }

    private void applyToLoggers(LoggingConfiguration config) {
        if (loggerMap.isEmpty()) {
            return;
        }
        loggerMap.forEach((key, value) -> {
            if (config.getLevel() != null) {
                value.setLevel(config.getLevel());
            }

            if (config.getDestinations() != null && !config.getDestinations().isEmpty()) {
                value.setDestinations(config.getDestinations());
            }
        });
    }


    public void shutdown() {
        processor.stop();
        loggerMap.values().stream()
                .flatMap(logger -> logger.getDestinations().stream())
                .distinct()
                .forEach(OutputDestination::close);
        System.out.println("Logging framework shut down gracefully.");
    }
}
