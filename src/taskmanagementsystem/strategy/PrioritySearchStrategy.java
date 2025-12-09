package taskmanagementsystem.strategy;

import taskmanagementsystem.entity.Task;


import java.util.List;
import java.util.Map;

public class PrioritySearchStrategy<Priority> implements TaskSearchStrategy<Priority> {

    @Override
    public List<Task> search(Map<String, Task> taskMap, Priority key) {
      return taskMap.values().stream().filter(task->task.getPriority().equals(key)).toList();
    }
}
