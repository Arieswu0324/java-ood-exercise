package taskmanagementsystem.strategy;

import taskmanagementsystem.entity.Task;

import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Map;

public class DueDateSearchStrategy<LocalDate> implements TaskSearchStrategy<LocalDate> {
    @Override
    public List<Task> search(Map<String, Task> taskMap, LocalDate key) {
        return taskMap.values().stream().filter(task ->
                task.getDueDate().isBefore((ChronoLocalDate) key)
        ).toList();
    }
}
