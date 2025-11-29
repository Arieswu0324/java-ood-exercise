package loggingframework.entity;

import loggingframework.service.LogManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoggerFactory {

    private static final LogManager logManager = LogManager.getINSTANCE();
    private static final Map<Class, Logger> cache = new ConcurrentHashMap<>();

    public static Logger getLogger(Class clazz) {
        return cache.computeIfAbsent(clazz, k -> {
            Logger logger = new Logger(clazz);
            logManager.addToManager(logger);
            return logger;
        });
    }
}
