package trafficsignalcontrol;

import trafficsignalcontrol.service.Intersection;
import trafficsignalcontrol.service.TrafficControlSystem;
import trafficsignalcontrol.state.NorthSouthIntersectionState;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        TrafficControlSystem instance = TrafficControlSystem.getInstance();
        Intersection intersection1 = new Intersection.Builder(1)
                .withGreenDuration(500)
                .withYellowDuration(100)
                .initializeState(new NorthSouthIntersectionState())
                .build();

        Intersection intersection2 = new Intersection.Builder(2).withGreenDuration(300).withYellowDuration(100).build();

        instance.addIntersection(intersection1);
        instance.addIntersection(intersection2);

        instance.start();

        Thread.sleep(5000);

        instance.shutdown();
    }
}
