package taskmanagementsystem.strategy;

import taskmanagementsystem.entity.Task;
import taskmanagementsystem.entity.User;

import java.util.List;
import java.util.Map;

public class UserSearchStrategy implements TaskSearchStrategy<User> {
    @Override
    public List<Task> search(Map<String, Task> map, User key) {
        return map.values().stream().filter(task -> task.getAssignee().equals(key)).toList();
    }
}
