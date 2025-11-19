package parkinglot.entity;

import parkinglot.enums.Type;

public class Motor extends Vehicle {

    public Motor(String plate) {
        super(Type.MEDIUM, plate);
    }
}
