package taskmanagementsystem.strategy;

import taskmanagementsystem.entity.Task;

import java.util.List;
import java.util.Map;

public interface TaskSearchStrategy<K> {
    List<Task> search(Map<String, Task> taskMap, K key);
}
