package parkinglot;

import java.util.ArrayList;
import java.util.List;

public class NearFirstStrategy implements ParkingStrategy {

    @Override
    public ParkingSpot find(List<ParkingFloor> floors, Vehicle vehicle) {
        List<ParkingSpot> spot = new ArrayList<>(1);
        boolean find = false;
        for (ParkingFloor floor : floors) {
            switch (vehicle.type) {
                case SMALL -> {
                    if (!floor.getSpots().get(SportSize.SMALL).isEmpty()) {
                        spot.add(floor.getSpots().get(SportSize.SMALL).getFirst());
                        find = true;
                    }
                }
                case MEDIUM -> {
                    if (!floor.getSpots().get(SportSize.MEDIUM).isEmpty()) {
                        spot.add(floor.getSpots().get(SportSize.MEDIUM).getFirst());
                        find = true;
                    }
                }

                case LARGE -> {
                    if (!floor.getSpots().get(SportSize.LARGE).isEmpty()) {
                        spot.add(floor.getSpots().get(SportSize.LARGE).getFirst());
                        find = true;
                    }
                }
            }
            if (find) {
                break;
            }
        }
        return spot.getFirst();
    }
}
