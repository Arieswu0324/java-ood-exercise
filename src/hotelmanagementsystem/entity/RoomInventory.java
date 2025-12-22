package hotelmanagementsystem.entity;

import hotelmanagementsystem.enums.RoomType;
import hotelmanagementsystem.exception.HotelManagementException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomInventory {

    //日期 - 类型 - 库存
    private final Map<LocalDate, Map<RoomType, Integer>> inventory;


    public RoomInventory(int dateCount, Map<RoomType, Integer> roomMap) {
        inventory = new ConcurrentHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < dateCount; i++) {
            LocalDate date = today.plusDays(i);
            this.inventory.put(date, roomMap);
        }
    }

    public synchronized boolean tryReserve(RoomType type, int count, LocalDate start, LocalDate end) {
        long nights = ChronoUnit.DAYS.between(start, end);
        ;
        for (int i = 0; i < nights; i++) {
            Map<RoomType, Integer> typeMap = inventory.get(start.plusDays(i));
            Integer currentNumber = typeMap.get(type);
            if (currentNumber != null && currentNumber >= count) {
                continue;//当前日期可预订
            } else {
                return false;
            }

        }
        return true;
    }


    public synchronized void reserve(RoomType type, int count, LocalDate start, LocalDate end) {
        long nights = ChronoUnit.DAYS.between(start, end);
        for (long i = 0; i < nights; i++) {
            Map<RoomType, Integer> typeMap = inventory.get(start.plusDays(i));
            Integer currentNumber = typeMap.get(type);
            if (currentNumber != null && currentNumber >= count) {
                typeMap.put(type, typeMap.get(type) - count);
            } else {
                throw new HotelManagementException("insufficient rooms on date: " + start.plusDays(i));
            }

        }

    }

    public synchronized void release(RoomType type, int count, LocalDate start, LocalDate end) {
        long nights = ChronoUnit.DAYS.between(start, end);
        for (long i = 0; i < nights; i++) {
            Map<RoomType, Integer> typeMap = inventory.get(start.plusDays(i));
            typeMap.put(type, typeMap.get(type) + count);
        }

    }
}
