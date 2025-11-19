package parkinglot.strategy;

import parkinglot.entity.Vehicle;
import parkinglot.entity.ParkingFloor;
import parkinglot.entity.ParkingSpot;

import java.util.List;
import java.util.Optional;

public interface ParkingStrategy {

  Optional<ParkingSpot> find(List<ParkingFloor> availableSpots, Vehicle vehicle);
}
