package parkinglot.observers;

import parkinglot.ParkingLotSystem;
import parkinglot.entity.ParkingSpot;
import parkinglot.enums.SpotSize;

import java.util.Optional;
import java.util.Set;

public class MotorPullObserver implements ParkingLotPullObserver {
    private final String plate;

    private static final Set<SpotSize> INTERESTED_SPOTS = Set.of(SpotSize.MEDIUM, SpotSize.LARGE);

    public MotorPullObserver(String plate) {
        this.plate = plate;
    }

    public String getPlate() {
        return plate;
    }

    @Override
    public void onAvailableSpot(ParkingLotSystem subject) {
        for (SpotSize size : INTERESTED_SPOTS) {
            Optional<ParkingSpot> spotOptional = subject.getAvailableSpot(size);
            if (spotOptional.isPresent()) {
                System.out.println("Motor" + getPlate() + " 查询到 " + size + "车位可用");
            }
        }
    }

    @Override
    public Set<SpotSize> getInterestedSpot() {
        return INTERESTED_SPOTS;
    }
}
