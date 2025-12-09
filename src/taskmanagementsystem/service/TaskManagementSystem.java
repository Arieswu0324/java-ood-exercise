package taskmanagementsystem.service;

import taskmanagementsystem.entity.Task;
import taskmanagementsystem.entity.TaskUpdateContext;
import taskmanagementsystem.entity.User;
import taskmanagementsystem.enums.Priority;
import taskmanagementsystem.enums.TaskStatus;
import taskmanagementsystem.exception.*;
import taskmanagementsystem.specification.SearchCriteria;
import taskmanagementsystem.strategy.DueDateSearchStrategy;
import taskmanagementsystem.strategy.PrioritySearchStrategy;
import taskmanagementsystem.strategy.TaskSearchStrategy;
import taskmanagementsystem.strategy.UserSearchStrategy;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class TaskManagementSystem {
    private static final TaskManagementSystem INSTANCE = new TaskManagementSystem();

    protected TaskManagementSystem() {
    }

    public static TaskManagementSystem getInstance() {
        return INSTANCE;
    }

    private final ReentrantLock lock = new ReentrantLock();

    private final DueNotifyProcessor processor = new DueNotifyProcessor();

    private final Map<String, Task> taskMap = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public void startSystem() {
        scheduler.scheduleWithFixedDelay(new RemindTask(), 8, 24, TimeUnit.HOURS);
    }

    public void createTask(Task task) {
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new InsufficientTaskInfoException("title");
        }
        if (task.getPriority() == null) {
            throw new InsufficientTaskInfoException("priority");
        }

        if (task.getCreator() == null) {
            throw new InsufficientTaskInfoException("creator");
        }

        //去掉锁，降低颗粒度
        Task existingTask = taskMap.putIfAbsent(task.getTitle(), task);

        if (existingTask != null) {
            throw new DuplicateTaskNameException();
        }
    }

    public void assignTask(Task task, User user, User assignee) {
        if (user == null) {
            throw new EmptyUserException();
        }

        if (assignee == null) {
            throw new InsufficientTaskInfoException("assignee");
        }
        lock.lock();
        try {
            task.assignTask(user, assignee);
        } finally {
            lock.unlock();
        }
    }

    public void updateTask(String name, TaskUpdateContext context, User user) {
        if (user == null) {
            throw new EmptyUserException();
        }
        //这里不需要锁，因为lock保护的是taskMap，但此处并没有对map的
        if (!taskMap.containsKey(name)) {
            throw new InsufficientTaskInfoException("title");
        }
        Task task = taskMap.get(name);
        task.updateTask(context, user);
    }

    public void startTask(Task task, User user) {
        //validations...
        task.start(user);

    }

    public void completeTask(Task task, User user) {
        //validations...
        task.markComplete(user);

    }

    public void deleteTask(String name, User user) {
        if (user == null) {
            throw new EmptyUserException();
        }
        lock.lock();
        try {
            if (!taskMap.containsKey(name)) {
                throw new TaskNotExistException(name);
            }
            Task task = taskMap.get(name);
            try {
                taskMap.remove(name);//内存应用，采用物理删除
                if (task.getStatus().equals(TaskStatus.COMPLETED.name())) {
                    task.getAssignee().removeFromHistory(name);
                }
            } catch (Exception e) {
                //回滚
                taskMap.put(task.getTitle(), task);
                if (task.getStatus().equals(TaskStatus.COMPLETED.name())) {
                    task.getAssignee().addToHistory(task);
                }
                System.out.println("Error happened when deleting task, rolled back" + e.getMessage());
            }
            System.out.println("Task " + name + " deleted by " + user.getName());
        } finally {
            lock.unlock();
        }
    }


    public <K> List<Task> search(K key) {
        TaskSearchStrategy searchStrategy;
        switch (key) {
            case Priority priority -> searchStrategy = new PrioritySearchStrategy();
            case LocalDate localDate -> searchStrategy = new DueDateSearchStrategy();
            case User user -> searchStrategy = new UserSearchStrategy();
            case null, default -> throw new UnsupportedSearchOperationException();
        }

        return searchStrategy.search(Collections.unmodifiableMap(taskMap), key);
    }

    public List<Task> search(SearchCriteria criteria) {
        if (criteria == null) {
            throw new IllegalArgumentException("Search criteria cannot be null");
        }

        return taskMap.values().stream()
                .filter(criteria::matches)
                .collect(Collectors.toList());
    }


    public void stopSystem() {
        processor.shutdown();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            scheduler.shutdownNow();
        }
    }

    private class RemindTask implements Runnable {

        @Override
        public void run() {
            List<Task> dueList = taskMap.values().stream().filter(task ->
                    task.getDueDate().isBefore(LocalDate.now().plusDays(2))//2可以改成可配置的
            ).toList();

            if (!dueList.isEmpty()) {
                processor.remind(dueList);
            }
        }
    }
}
