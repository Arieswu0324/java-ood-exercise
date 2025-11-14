package parkinglot;

public abstract class Vehicle {
    private final Type type;
    private final String plate;


    public Vehicle(Type type, String plate) {
        this.type = type;
        this.plate = plate;
    }

    public String getPlate() {
        return plate;
    }


    public Type getType() {
        return type;
    }


}
