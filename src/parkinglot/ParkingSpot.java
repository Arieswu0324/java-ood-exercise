package parkinglot;

import java.util.concurrent.atomic.AtomicBoolean;

public class ParkingSpot {
    private final int id;
    private final SpotSize size;


    private final AtomicBoolean isOccupied = new AtomicBoolean(false);


    public boolean occupy() {
        return isOccupied.compareAndSet(false, true);
    }

    public void release() {
        isOccupied.set(false);
    }

    ParkingSpot(int id, SpotSize size) {
        this.id = id;
        this.size = size;
    }


    public SpotSize getSize() {
        return size;
    }

    public boolean isOccupied(){
        return isOccupied.get();
    }

}
