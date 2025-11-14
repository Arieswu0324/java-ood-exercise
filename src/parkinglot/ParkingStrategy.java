package parkinglot;

import java.util.List;
import java.util.Optional;

public interface ParkingStrategy {

  Optional<ParkingSpot> find(List<ParkingFloor> availableSpots, Vehicle vehicle);
}
