package taskmanagementsystem.entity;


import taskmanagementsystem.enums.Priority;
import taskmanagementsystem.exception.InsufficientTaskInfoException;
import taskmanagementsystem.state.PendingState;
import taskmanagementsystem.state.TaskState;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static taskmanagementsystem.enums.Priority.*;

//应该使用ID作为primary key
public class Task {
    private static final Map<Priority, Integer> DUE_MAP = Map.of(P0, 1,
            P1, 2,
            P2, 7,
            P3, 14,
            P4, 28);


    private final String title;
    private volatile String description;
    private volatile LocalDate dueDate;
    private volatile Priority priority;

    private LocalDate startDate;
    private LocalDate endDate;
    private final LocalDate createdDate;

    private LocalDate lastUpdatedDate;
    private User modifiedBy;
    private final User creator;

    private volatile TaskState taskState;

    private volatile User assignee;

    private final ReentrantLock lock = new ReentrantLock();


    private Task(String title, String description, Priority priority, User creator) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = LocalDate.now().plusDays(DUE_MAP.get(priority));
        this.taskState = new PendingState();
        this.creator = creator;
        this.createdDate = LocalDate.now();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public void start(User user) {
        taskState.start(this, user);
    }

    public void markComplete(User user) {
        taskState.markComplete(this, user);
    }

    public void assignTask(User user, User assignee) {
        this.taskState.assignTask(this, user, assignee);
    }

    public void updateTask(TaskUpdateContext context, User user) {
        taskState.update(this, context, user);
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }


    public String getTitle() {
        return title;
    }

    public Priority getPriority() {
        return priority;
    }

    public LocalDate getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public String getDescription() {
        return description;
    }

    public User getAssignee() {
        return assignee;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return taskState.getStatus();
    }

    public void setLastUpdatedDate(LocalDate date) {
        this.lastUpdatedDate = date;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(Priority priority) {
        lock.lock();
        try {
            this.priority = priority;
            updateDueDate();
        } finally {
            lock.unlock();
        }
    }


    public User getCreator() {
        return creator;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }


    private void updateDueDate() {
        this.dueDate = this.createdDate.plusDays(DUE_MAP.get(priority));
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }

    public void setModifiedBy(User user) {
        this.modifiedBy = user;
    }

    public void setStartDate(LocalDate now) {
        this.startDate = now;
    }

    public void setEndDate(LocalDate now) {
        this.endDate = now;
    }

    public static class Builder {
        private String title;
        private String description = "";
        private Priority priority = P3;
        private User user;


        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setPriority(Priority priority) {
            this.priority = priority;
            return this;
        }


        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder createdBy(User user) {
            this.user = user;
            return this;
        }

        public Task build() {
            if (this.title == null || title.isBlank()) {
                throw new InsufficientTaskInfoException("title");
            }
            return new Task(this.title, this.description, this.priority, this.user);
        }
    }
}
