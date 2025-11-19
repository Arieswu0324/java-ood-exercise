package parkinglot.entity;


import parkinglot.enums.Type;

public class Car extends Vehicle {

    public Car(String plate) {
        super(Type.SMALL, plate);
    }


}
