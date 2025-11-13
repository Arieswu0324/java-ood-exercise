package parkinglot;

import java.util.List;

public interface ParkingStrategy {

    public ParkingSpot find(List<ParkingFloor> availableSpots, Vehicle vehicle);
}
