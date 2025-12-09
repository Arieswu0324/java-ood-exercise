package taskmanagementsystem.specification;

import taskmanagementsystem.entity.Task;
import taskmanagementsystem.enums.Priority;

public class PrioritySearchCriteria implements SearchCriteria {
    private final Priority priority;

    public PrioritySearchCriteria(Priority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        this.priority = priority;
    }

    @Override
    public boolean matches(Task task) {
        return priority.equals(task.getPriority());
    }
}
