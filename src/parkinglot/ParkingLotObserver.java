package parkinglot;

import java.util.Set;

public interface ParkingLotObserver {
    Set<SpotSize> getInterestedSpot();
}
