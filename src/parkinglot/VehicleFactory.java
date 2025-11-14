package parkinglot;

public abstract class VehicleFactory {
    protected abstract Vehicle create(String plate);
}
