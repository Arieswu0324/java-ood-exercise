package hotelmanagementsystem.state;

import hotelmanagementsystem.entity.PaymentInfo;
import hotelmanagementsystem.entity.Reservation;
import hotelmanagementsystem.entity.Room;
import hotelmanagementsystem.enums.RoomStatus;
import hotelmanagementsystem.entity.User;
import hotelmanagementsystem.exception.IllegalOperationException;

import java.util.List;

public class AvailableState implements RoomState {
    private final RoomStatus status = RoomStatus.AVAILABLE;

    @Override
    public void reserve(Room room, Reservation reservation) {
        room.setReservation(reservation);
    }

    @Override
    public void checkin(Room room, List<User> guests, PaymentInfo paymentInfo) {
        throw new IllegalOperationException();
    }

    @Override
    public void checkout(Room room) {
        throw new IllegalOperationException();
    }

    @Override
    public RoomStatus getStatus() {
        return status;
    }


}
