package hotelmanagementsystem;

import hotelmanagementsystem.entity.Room;
import hotelmanagementsystem.entity.Staff;
import hotelmanagementsystem.entity.User;
import hotelmanagementsystem.enums.RoomType;
import hotelmanagementsystem.exception.HotelManagementException;
import hotelmanagementsystem.service.HotelManagementSystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        List<Room> single = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Room room = new Room(String.valueOf(i), RoomType.SINGLE);
            single.add(room);
        }

        Map<RoomType, List<Room>> roomMap = new HashMap<>();
        roomMap.put(RoomType.SINGLE, single);
        HotelManagementSystem instance = new HotelManagementSystem(roomMap);

        instance.initializeInventory(30);

        Staff staff = new Staff("abc", "abc@hotel.com");

        instance.addStaff(staff);

        User guest = new User("guest1", "guest1@some.com");

        instance.reserve(RoomType.SINGLE, 1, guest, LocalDate.of(2025, 12, 24), LocalDate.of(2025, 12, 26));


    }

}
