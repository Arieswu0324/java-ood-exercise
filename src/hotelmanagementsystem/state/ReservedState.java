package hotelmanagementsystem.state;

import hotelmanagementsystem.entity.PaymentInfo;
import hotelmanagementsystem.entity.Reservation;
import hotelmanagementsystem.entity.Room;
import hotelmanagementsystem.entity.User;
import hotelmanagementsystem.enums.RoomStatus;
import hotelmanagementsystem.exception.IllegalOperationException;

import java.time.LocalDateTime;
import java.util.List;

public class ReservedState implements RoomState {
    @Override
    public void reserve(Room room, Reservation reservation) {
        throw new IllegalOperationException();
    }

    @Override
    public void checkin(Room room, List<User> guests, PaymentInfo paymentInfo) {
        Reservation reservation = room.getReservation();
        reservation.setCheckIn(LocalDateTime.now());
        reservation.setPaymentInfo(paymentInfo);
        reservation.setGuests(guests);
    }

    @Override
    public void checkout(Room room) {
        throw new IllegalOperationException();
    }

    @Override
    public RoomStatus getStatus() {
        return null;
    }
}
