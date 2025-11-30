package trafficsignalcontrol.entity;

import trafficsignalcontrol.enums.Direction;
import trafficsignalcontrol.enums.Signal;

public class TrafficLight {
    private volatile Signal signal;
    private final int intersectionId;
    private final Direction direction;

    public TrafficLight(int intersectionId, Direction direction) {
        this.intersectionId = intersectionId;
        this.direction = direction;
        this.signal = Signal.RED;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getIntersectionId() {
        return intersectionId;
    }

    public void setSignal(Signal signal) {
        this.signal = signal;
    }

    public Signal getSignal() {
        return this.signal;
    }
}
