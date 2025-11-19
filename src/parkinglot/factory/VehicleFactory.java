package parkinglot.factory;

import parkinglot.entity.Vehicle;

public abstract class VehicleFactory {

    public Vehicle createVehicle(String plate) {
        System.out.println("some preparation steps");
        Vehicle vehicle = create(plate);
        System.out.println(vehicle.getPlate() + "vehicle created");
        return vehicle;
    }

    protected abstract Vehicle create(String plate);
}
