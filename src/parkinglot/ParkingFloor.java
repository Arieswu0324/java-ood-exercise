package parkinglot;

import java.util.List;
import java.util.Map;

public class ParkingFloor {
    private int id;

    private Map<SportSize, List<ParkingSpot>> spots;

    public ParkingFloor(int id){
        this.id = id;
    }

    public Map<SportSize, List<ParkingSpot>> getSpots() {
        return this.spots;
    }

    public void setSpots(Map<SportSize, List<ParkingSpot>> spots) {
        this.spots = spots;
    }

}
