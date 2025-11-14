package parkinglot;

public class CarFactory extends VehicleFactory {
    private static final CarFactory INSTANCE = new CarFactory();

    //eager initialization
    public static CarFactory getInstance() {
        return INSTANCE;
    }

    @Override
    protected Vehicle create(String plate) {
        return new Car(plate);
    }
}
