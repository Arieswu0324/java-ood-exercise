package hotelmanagementsystem.state;

import hotelmanagementsystem.entity.PaymentInfo;
import hotelmanagementsystem.entity.Reservation;
import hotelmanagementsystem.entity.Room;
import hotelmanagementsystem.entity.User;
import hotelmanagementsystem.enums.ReservationStatus;
import hotelmanagementsystem.enums.RoomStatus;
import hotelmanagementsystem.exception.IllegalOperationException;

import java.time.LocalDateTime;
import java.util.List;

public class CheckedInState implements RoomState {
    private final RoomStatus status = RoomStatus.AVAILABLE;

    @Override
    public void reserve(Room room, Reservation reservation) {
        throw new IllegalOperationException();

    }

    @Override
    public void checkin(Room room, List<User> guests, PaymentInfo paymentInfo) {
        throw new IllegalOperationException();
    }

    @Override
    public void checkout(Room room) {
        Reservation reservation = room.getReservation();
        reservation.setCheckout(LocalDateTime.now());
        reservation.bill();
        reservation.setStatus(ReservationStatus.ARCHIVED);
        room.archiveReservation();
    }

    @Override
    public RoomStatus getStatus() {
        return status;
    }
}
