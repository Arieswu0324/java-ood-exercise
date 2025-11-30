package trafficsignalcontrol.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TrafficControlSystem {

    private static final TrafficControlSystem INSTANCE = new TrafficControlSystem();
    private ExecutorService executor;

    private final List<Intersection> intersections = new ArrayList<>();


    private TrafficControlSystem() {
    }

    public static TrafficControlSystem getInstance() {
        return INSTANCE;
    }

    public void addIntersection(Intersection intersection) {
        intersections.add(intersection);
    }

    public void start() {
        executor = Executors.newFixedThreadPool(intersections.size());
        intersections.forEach(executor::submit);
        System.out.println("System start");
    }

    public void shutdown() {
        intersections.forEach(Intersection::stop);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("System shut down");
    }
}
