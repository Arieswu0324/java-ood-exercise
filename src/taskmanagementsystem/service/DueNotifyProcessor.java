package taskmanagementsystem.service;

import taskmanagementsystem.entity.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DueNotifyProcessor {
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    public void remind(List<Task> tasks) {
        tasks.forEach(task -> {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("sending out email to " + task.getAssignee().getEmail());
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
