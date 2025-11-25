package parkinglot.observers;

import parkinglot.entity.SpotAvailableEvent;
import parkinglot.enums.SpotSize;

import java.util.Set;

public class CarPushObserver implements ParkingLotPushObserver {
    String plate;
    private static final Set<SpotSize> INTERESTED_SPOTS = Set.of(SpotSize.SMALL, SpotSize.MEDIUM, SpotSize.LARGE);

    public CarPushObserver(String plate) {
        this.plate = plate;
    }

    public String getPlate() {
        return plate;
    }

    @Override
    public void onAvailableSpot(SpotAvailableEvent event) {
        System.out.println("car : " + getPlate() + "收到通知，有" + event.getSpot().getSize() + "车位空出");
    }

    @Override
    public Set<SpotSize> getInterestedSpot() {
        return INTERESTED_SPOTS;
    }


}
