package parkinglot;

public class TruckFactory extends VehicleFactory {

    private static final TruckFactory INSTANCE = new TruckFactory();

    private TruckFactory() {
    }

    public TruckFactory getInstance() {
        return INSTANCE;
    }

    @Override
    protected Vehicle create(String plate) {
        return new Truck(plate);
    }
}
