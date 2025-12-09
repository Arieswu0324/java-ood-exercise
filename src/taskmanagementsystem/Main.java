package taskmanagementsystem;

import taskmanagementsystem.entity.Task;
import taskmanagementsystem.entity.TaskUpdateContext;
import taskmanagementsystem.entity.User;
import taskmanagementsystem.enums.Priority;
import taskmanagementsystem.service.TaskManagementSystem;
import taskmanagementsystem.specification.AssigneeSearchCriteria;
import taskmanagementsystem.specification.PrioritySearchCriteria;
import taskmanagementsystem.specification.SearchCriteria;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManagementSystem system = TaskManagementSystem.getInstance();
        system.startSystem();

        User user = new User("test1234", "test1234@test.com");
        User assignee = new User("abc", "abc@test.com");

        Task task = Task.newBuilder().withTitle("task").createdBy(user).build();

        system.createTask(task);
        system.assignTask(task, user, assignee);

        system.startTask(task, assignee);

        system.updateTask(task.getTitle(), new TaskUpdateContext("updated description", null), user);

        system.completeTask(task, assignee);

        List<Task> list = system.search(assignee);
        System.out.println(list.size());

        // 2. 组合搜索
        SearchCriteria highPriority =
                new PrioritySearchCriteria(Priority.P0)
                        .and(new AssigneeSearchCriteria(assignee));
        List<Task> urgentTasks = system.search(highPriority);
        System.out.println(urgentTasks.size());

        List<Task> history = user.getHistory();
        System.out.println(history.size());

        system.deleteTask(task.getTitle(), user);
        history = user.getHistory();
        System.out.println(history.size());

        system.stopSystem();
    }

}
