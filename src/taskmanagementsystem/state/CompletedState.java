package taskmanagementsystem.state;

import taskmanagementsystem.entity.Task;
import taskmanagementsystem.entity.TaskUpdateContext;
import taskmanagementsystem.entity.User;
import taskmanagementsystem.enums.TaskStatus;
import taskmanagementsystem.exception.UnsupportedStatusException;

public class CompletedState implements TaskState {
    private final TaskStatus status = TaskStatus.COMPLETED;


    @Override
    public void start(Task task, User user) {
        throw new UnsupportedStatusException(status.name());
    }

    @Override
    public void markComplete(Task task, User user) {
        throw new UnsupportedStatusException(status.name());
    }

    @Override
    public void update(Task task, TaskUpdateContext context, User user) {
        throw new UnsupportedStatusException(status.name());
    }

    @Override
    public void assignTask(Task task, User user, User assignee) {
        throw new UnsupportedStatusException(status.name());
    }

    @Override
    public String getStatus() {
        return status.name();
    }
}
