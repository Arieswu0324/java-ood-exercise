package parkinglot;

import java.util.Set;

/**
 * 按车位类型订阅
 * */
public interface ParkingLotObserver {

   void onAvailableSpot(SpotAvailableEvent event);

   Set<SpotSize> getInterestedSpot();
}
