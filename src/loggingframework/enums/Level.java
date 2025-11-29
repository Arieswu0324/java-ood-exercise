package loggingframework.enums;

public enum Level {
    DEBUG(0), INFO(1), WARNING(2), ERROR(3), FATAL(4);

    private final int priority;

    Level(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public boolean shouldLog(Level threshold) {
        return this.priority >= threshold.priority;
    }
}
