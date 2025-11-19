package parkinglot.entity;

import parkinglot.enums.SpotSize;

import java.util.List;
import java.util.Map;

public class ParkingFloor {
    private int id;

    private Map<SpotSize, List<ParkingSpot>> spots;

    public ParkingFloor(int id){
        this.id = id;
    }

    public Map<SpotSize, List<ParkingSpot>> getSpots() {
        return this.spots;
    }

    public void setSpots(Map<SpotSize, List<ParkingSpot>> spots) {
        this.spots = spots;
    }

}
