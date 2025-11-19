package parkinglot.factory;

import parkinglot.entity.Motor;
import parkinglot.entity.Vehicle;

public class MotorFactory extends VehicleFactory {

    private static final MotorFactory INSTANCE = new MotorFactory();

    private MotorFactory() {
    }

    public static MotorFactory getInstance() {
        return INSTANCE;
    }

    @Override
    protected Vehicle create(String plate) {
        return new Motor(plate);
    }
}
