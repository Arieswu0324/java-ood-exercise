package parkinglot.entity;

/**
 * Could notify customers about available spots.
 * 封装车位可用的事件信息，传递给观察者
 */
public class SpotAvailableEvent {
    private final ParkingSpot spot;
    private final long timestamp;


    public SpotAvailableEvent(ParkingSpot spot) {
        this.spot = spot;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ParkingSpot getSpot() {
        return spot;
    }
}
