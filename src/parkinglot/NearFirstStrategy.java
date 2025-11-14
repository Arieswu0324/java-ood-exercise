package parkinglot;

import java.util.List;
import java.util.Optional;

public class NearFirstStrategy implements ParkingStrategy {

    @Override
    public Optional<ParkingSpot> find(List<ParkingFloor> floors, Vehicle vehicle) {
        for (ParkingFloor floor : floors) {
            switch (vehicle.getType()) {

                case SMALL:
                    Optional<ParkingSpot> small = findAvailableSpotInList(floor, SpotSize.SMALL);
                    if (small.isPresent()) {
                        return small;
                    }
                case MEDIUM:
                    Optional<ParkingSpot> medium = findAvailableSpotInList(floor, SpotSize.MEDIUM);
                    if (medium.isPresent()) {
                        return medium;
                    }
                case LARGE:
                    Optional<ParkingSpot> large = findAvailableSpotInList(floor, SpotSize.LARGE);
                    if (large.isPresent()) {
                        return large;
                    }
            }

        }
        return Optional.empty();
    }

    private Optional<ParkingSpot> findAvailableSpotInList(ParkingFloor floor, SpotSize spotSize) {
        List<ParkingSpot> spots = floor.getSpots().get(spotSize);
        //这里线程不安全，当stream返回spot 时，它可能被其他线程占用了
//        return Optional.ofNullable(spots)
//                .flatMap(list -> list.stream().
//                        filter(spot -> !spot.isOccupied())
//                        .findFirst());
        if(spots==null){
            return Optional.empty();
        }
        for(ParkingSpot spot: spots){
            //CAS保证线程安全
            if(spot.occupy()){
                return Optional.of(spot);
            }
        }
        return Optional.empty();
    }
}
