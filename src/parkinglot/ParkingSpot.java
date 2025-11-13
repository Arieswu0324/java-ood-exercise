package parkinglot;

public class ParkingSpot {
    private int id;
    private int floorId;
    private boolean isOccupied;
   private ParkingTicket ticket;
    private SportSize size;


    public int getId() {
        return id;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public ParkingTicket getTicket() {
        return ticket;
    }

    public SportSize getSize() {
        return size;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFloorId(int floorId) {
        this.floorId = floorId;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public void setSize(SportSize size) {
        this.size = size;
    }

    public void setTicket(ParkingTicket ticket) {
        this.ticket = ticket;
    }
}
