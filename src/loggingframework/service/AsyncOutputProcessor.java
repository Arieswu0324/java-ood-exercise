package loggingframework.service;

import loggingframework.entity.LoggingMessage;
import loggingframework.output.OutputDestination;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AsyncOutputProcessor {

    private final ExecutorService executor;

    public AsyncOutputProcessor() {
        this.executor =
                new ThreadPoolExecutor(3, 5, 2, TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(10));
    }

    public void process(LoggingMessage message, List<OutputDestination> destinations) {
        //创建快照，避免迭代时被修改
        List<OutputDestination> snapshot = new ArrayList<>(destinations);

        try {
            executor.execute(() -> {
                        snapshot.forEach(d -> {
                            try {
                                d.output(message);
                            } catch (Exception e) {
                                System.err.println("failed to output to destination: " + d.getClass().getName() + ", error:" + e.getMessage());
                            }
                        });
                    }
            );
        } catch (RejectedExecutionException e) {
            System.err.println("failed to submit, executor queue is full, error: " + e.getMessage());
        }

    }

    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
                System.err.println("Logger executor did not terminate in the specified time.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
