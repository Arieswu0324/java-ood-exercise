package trafficsignalcontrol;

import trafficsignalcontrol.service.Intersection;
import trafficsignalcontrol.service.TrafficControlSystem;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        TrafficControlSystem instance = TrafficControlSystem.getInstance();

        instance.addIntersection(new Intersection(1, 600, 100));
        instance.addIntersection(new Intersection(2, 300, 50));

        instance.start();

        Thread.sleep(5000);

        instance.shutdown();
    }
}
