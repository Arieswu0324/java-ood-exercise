package parkinglot.entity;

import parkinglot.enums.Type;

public class Truck extends Vehicle {

    public Truck(String plate) {
        super(Type.LARGE, plate);
    }
}
