package taskmanagementsystem.state;

import taskmanagementsystem.entity.Task;
import taskmanagementsystem.entity.User;
import taskmanagementsystem.enums.TaskStatus;
import taskmanagementsystem.exception.UnsupportedStatusException;
import taskmanagementsystem.exception.UnsupportedUserOperationException;

import java.time.LocalDate;

public class PendingState implements TaskState {
    private final TaskStatus status = TaskStatus.PENDING;


    @Override
    public void start(Task task, User user) {
        if (!task.getAssignee().equals(user)) {
            throw new UnsupportedUserOperationException();
        }
        task.setModifiedBy(user);
        task.setLastUpdatedDate(LocalDate.now());
        task.setStartDate(LocalDate.now());
        task.setTaskState(new InProgressState());
    }

    @Override
    public void markComplete(Task task, User user) {
        throw new UnsupportedStatusException(status.name());
    }

    @Override
    public String getStatus() {
        return status.name();
    }
}
