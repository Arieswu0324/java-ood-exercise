package hotelmanagementsystem.service;

import hotelmanagementsystem.entity.*;

import hotelmanagementsystem.enums.ReservationStatus;
import hotelmanagementsystem.enums.RoomType;
import hotelmanagementsystem.exception.HotelManagementException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HotelManagementSystem {

    //current room map
    private final Map<RoomType, List<Room>> roomMap = new ConcurrentHashMap<>();

    private final Map<String, Reservation> reservationMap = new ConcurrentHashMap<>();

    private final Map<RoomType, Long> priceMap = new ConcurrentHashMap<>();

    private final Map<String, Staff> staffMap = new ConcurrentHashMap<>();

    private RoomInventory inventory = null;

    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    private final ThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(4);


    public HotelManagementSystem(Map<RoomType, List<Room>> roomMap) {
        this.roomMap.putAll(roomMap);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                reservationMap.forEach((key, value) -> {
                    if (value.getStartDate().isAfter(LocalDate.now()) && value.getStatus() == ReservationStatus.SUCCESS) {
                        threadPoolExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                cancelReservation(key);
                            }
                        });
                    }
                });

            }
        }, 1, TimeUnit.DAYS);
    }

    public Map<RoomType, List<Room>> getCurrenntRoomMap() {
        return new HashMap<>(roomMap);
    }

    public void initializeInventory(int dateCount) {
        Map<RoomType, Integer> map = new HashMap<>();
        roomMap.forEach((key, value) -> map.put(key, value.size()));
        this.inventory = new RoomInventory(dateCount, map);
    }

    public void setPrice(RoomType type, long price) {
        priceMap.put(type, price);
    }

    public void addStaff(Staff staff) {
        staffMap.putIfAbsent(staff.getStaffId(), staff);
    }

    public void removeStaff(Staff staff) {
        staffMap.remove(staff.getStaffId());
    }

    //id 是reservation id
    public void assignRoom(List<Room> rooms, String id, Staff staff) {
        validateStaff(staff);

        if (!reservationMap.containsKey(id)) {
            throw new HotelManagementException("reservation does not exist");
        }

        Reservation reservation = reservationMap.get(id);

        List<String> numbers = rooms.stream().map(Room::getNumber).toList();
        reservation.setRoomNumbers(numbers);
        rooms.forEach(it -> it.setReservation(reservation));
    }

    public void checkIn(Room room, List<User> guests, PaymentInfo paymentInfo, Staff staff) {
        validateStaff(staff);
        room.checkIn(guests, paymentInfo);
    }

    public void checkout(Room room, Staff staff) {
        validateStaff(staff);
        room.checkout();
    }


    public Reservation reserve(RoomType type, int count, User reserver, LocalDate start, LocalDate end) {
        synchronized (this) {

            if (inventory.tryReserve(type, count, start, end)) {
                inventory.reserve(type, count, start, end);
            } else {
                return null;
            }
        }

        Reservation.Builder builder = new Reservation.Builder();

        Reservation reservation = builder.withType(type)
                .by(reserver)
                .startFrom(start).endTo(end)
                .withPrice(priceMap.get(type))
                .build();
        reservationMap.put(reserver.getId(), reservation);
        return reservation;
    }


    public void cancelReservation(String id) {
        Reservation reservation = reservationMap.get(id);
        if (reservation == null) {
            throw new HotelManagementException("invalid reservation");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        this.inventory.release(reservation.getType(), reservation.getRoomCount(), reservation.getStartDate(), reservation.getEndDate());
    }

    private void validateStaff(Staff staff) {
        if (staff == null || staff.getStaffId() == null || !staffMap.containsKey(staff.getStaffId())) {
            throw new HotelManagementException("illegal staff");
        }
    }

    public void shutDown() {

        executor.shutdown();
        try {
            if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }

        threadPoolExecutor.shutdown();
        try {
            if (!threadPoolExecutor.awaitTermination(15, TimeUnit.SECONDS)) {
                threadPoolExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            threadPoolExecutor.shutdownNow();
        }
    }
}



