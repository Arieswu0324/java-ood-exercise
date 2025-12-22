package hotelmanagementsystem.state;

import hotelmanagementsystem.entity.PaymentInfo;
import hotelmanagementsystem.entity.Reservation;
import hotelmanagementsystem.entity.Room;
import hotelmanagementsystem.entity.User;
import hotelmanagementsystem.enums.RoomStatus;

import java.util.List;

public interface RoomState {

    void reserve(Room room, Reservation reservation);

    void checkin(Room room, List<User> guests, PaymentInfo paymentInfo);

    void checkout(Room room);

    RoomStatus getStatus();

}
