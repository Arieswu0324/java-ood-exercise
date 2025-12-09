package taskmanagementsystem.service;

import taskmanagementsystem.entity.Task;

import java.util.List;
import java.util.concurrent.*;

public class DueNotifyProcessor {
    private final ExecutorService executor = new ThreadPoolExecutor(5, 10,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public void remind(List<Task> tasks) {
        tasks.forEach(task -> {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("sending out email to " + task.getAssignee().getEmail());//这里需要assignee 校验
                }
            });
        });

    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                Thread.currentThread().interrupt();
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

}
