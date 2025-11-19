package parkinglot.observers;


import parkinglot.ParkingLotSystem;

public interface ParkingLotPullObserver extends ParkingLotObserver {

    void onAvailableSpot(ParkingLotSystem subject);

}
