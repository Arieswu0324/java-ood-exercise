package parkinglot;

import java.util.Set;

public class CarObserver implements ParkingLotObserver{
    String plate;
    private final Set<SpotSize> interestedSpot = Set.of(SpotSize.SMALL, SpotSize.MEDIUM, SpotSize.LARGE);

    CarObserver(String plate){
        this.plate = plate;
    }

    @Override
    public void onAvailableSpot(SpotAvailableEvent event) {
        System.out.println("car : " + getPlate() + "收到通知，有" + event.getSpot().getSize() + "车位空出");
    }

    @Override
    public Set<SpotSize> getInterestedSpot() {
        return interestedSpot;
    }

    public String getPlate(){
        return plate;
    }


}
