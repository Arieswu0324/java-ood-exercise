package parkinglot.observers;

import parkinglot.enums.SpotSize;

import java.util.Set;

public interface ParkingLotObserver {
    Set<SpotSize> getInterestedSpot();
}
