package hotelmanagementsystem.entity;

import hotelmanagementsystem.enums.RoomStatus;
import hotelmanagementsystem.enums.RoomType;
import hotelmanagementsystem.state.*;

import java.util.ArrayList;
import java.util.List;


public class Room {
    private final String number;
    private final RoomType type;
    private volatile long price;
    private RoomState state;
    private Reservation reservation;
    private final List<Reservation> history;

    public Room(String number, RoomType type) {
        this.number = number;
        this.state = new AvailableState();
        this.history = new ArrayList<>();
        this.type = type;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getPrice() {
        return this.price;
    }


    public String getNumber() {
        return number;
    }

    public RoomType getType() {
        return this.type;
    }


    public Reservation getReservation() {
        return this.reservation;
    }

    public void checkIn(List<User> guests, PaymentInfo paymentInfo) {
        state.checkin(this, guests, paymentInfo);
        state = new CheckedInState();
    }

    public void checkout() {
        state.checkout(this);
        state = new MaintenanceState();
    }

    public void clean() {
        System.out.println("room service completed");
        state = new AvailableState();
    }

    public void reserve(Reservation reservation) {
        state.reserve(this, reservation);
        state = new ReservedState();
    }

    public RoomStatus getStatus() {
        return state.getStatus();
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void archiveReservation() {
        history.add(reservation);
    }

    public List<Reservation> getHistory() {
        return new ArrayList<>(history);
    }
}
