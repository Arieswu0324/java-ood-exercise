package taskmanagementsystem.state;

import taskmanagementsystem.entity.Task;
import taskmanagementsystem.entity.User;
import taskmanagementsystem.enums.TaskStatus;
import taskmanagementsystem.exception.UnsupportedStatusException;
import taskmanagementsystem.exception.UnsupportedUserOperationException;

import java.time.LocalDate;

public class InProgressState implements TaskState {

    private final TaskStatus status = TaskStatus.IN_PROGRESS;

    @Override
    public void start(Task task, User user) {
        throw new UnsupportedStatusException(status.name());
    }

    @Override
    public void markComplete(Task task, User user) {
        if (!task.getAssignee().equals(user)) {
            throw new UnsupportedUserOperationException();
        }
        task.setLastUpdatedDate(LocalDate.now());
        task.setModifiedBy(user);
        task.setEndDate(LocalDate.now());
        task.setTaskState(new CompletedState());
        user.addToHistory(task);
    }

    @Override
    public String getStatus() {
        return status.name();
    }
}
