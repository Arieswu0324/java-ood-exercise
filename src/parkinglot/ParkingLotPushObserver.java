package parkinglot;

/**
 * 按车位类型订阅
 * */
public interface ParkingLotPushObserver extends ParkingLotObserver{

   void onAvailableSpot(SpotAvailableEvent event);
}
