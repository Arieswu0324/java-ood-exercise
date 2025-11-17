package parkinglot;



public interface ParkingLotPullObserver extends ParkingLotObserver{

    void onAvailableSpot(ParkingLotSystem subject);

}
