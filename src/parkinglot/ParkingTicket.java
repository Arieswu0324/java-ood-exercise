package parkinglot;

public class ParkingTicket {
    private int id;
    private long startTs;
    private long endTs;
    private Vehicle vehicle;
    private double rate;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setEndTs(long endTs) {
        this.endTs = endTs;
    }

    public long getEndTs() {
        return endTs;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    public void setStartTs(long startTs) {
        this.startTs = startTs;
    }

    public long getStartTs() {
        return startTs;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}
