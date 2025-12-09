package taskmanagementsystem.state;

import taskmanagementsystem.entity.Task;
import taskmanagementsystem.entity.TaskUpdateContext;
import taskmanagementsystem.entity.User;
import taskmanagementsystem.enums.TaskStatus;

import java.time.LocalDate;

public interface TaskState {

    void start(Task task, User user);

    void markComplete(Task task, User user);

    default void update(Task task, TaskUpdateContext context, User user){
        TaskUpdateContext copy = new TaskUpdateContext(context.description(),context.priority());
        User oldModifier = task.getModifiedBy();
        LocalDate oldModifiedTime = task.getLastUpdatedDate();

        try {
            if (context.description()!= null) {
                task.setDescription(context.description());
            }

            if (context.priority() != null) {
                task.setPriority(context.priority());
            }

            task.setModifiedBy(user);
            task.setLastUpdatedDate(LocalDate.now());

            if (task.getStatus().equals(TaskStatus.COMPLETED.name())) {
                task.getAssignee().addToHistory(task);
            }
        } catch (Exception e) {
            //回滚
            if (copy.description() != null) {
                task.setDescription(copy.description());
            }

            if (copy.priority() != null) {
                task.setPriority(copy.priority());
            }

            task.setModifiedBy(oldModifier);
            task.setLastUpdatedDate(oldModifiedTime);

            if (task.getStatus().equals(TaskStatus.COMPLETED.name())) {
                task.getAssignee().addToHistory(task);
            }

            System.out.println("Error happened when updating task, rolled back" + e.getMessage());
        }

        System.out.println("Task: " + task.getTitle() + " updated by " + user.getName());

    }

    default void assignTask(Task task, User user, User assignee){
        task.setAssignee(assignee);
        task.setModifiedBy(user);
        task.setLastUpdatedDate(LocalDate.now());
        System.out.println("Task: " + task.getTitle() + " assignee changed to " + assignee.getName());
    }

    String getStatus();
}
