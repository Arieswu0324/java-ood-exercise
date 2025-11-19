package parkinglot.factory;

import parkinglot.entity.Truck;
import parkinglot.entity.Vehicle;

public class TruckFactory extends VehicleFactory {

    private static final TruckFactory INSTANCE = new TruckFactory();

    private TruckFactory() {
    }

    public static TruckFactory getInstance() {
        return INSTANCE;
    }

    @Override
    protected Vehicle create(String plate) {
        return new Truck(plate);
    }
}
