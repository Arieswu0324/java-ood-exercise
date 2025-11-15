package parkinglot;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;


/**
 * OOD - Parking Lot
 */
public class ParkingLotSystem {

    // Use AtomicReference for thread-safe updates with immutable collections
    private final AtomicReference<List<ParkingFloor>> floors = new AtomicReference<>(Collections.emptyList());

    private final Map<String, ParkingTicket> tickets = new ConcurrentHashMap<>();

    private final AtomicReference<ParkingStrategy> strategy = new AtomicReference<>();

    private final AtomicReference<Map<SpotSize, Double>> rateMap = new AtomicReference<>(Collections.emptyMap());

    private static volatile ParkingLotSystem instance;

    //floor, type, list of spots
    private final Map<SpotSize, Integer> availableCounts = new ConcurrentHashMap<>();

    ParkingLotSystem() {
    }

    public static ParkingLotSystem getInstance() {
        if (instance == null) {
            synchronized (ParkingLotSystem.class) {
                if (instance == null) {
                    instance = new ParkingLotSystem();
                }
            }
        }
        return instance;
    }

    public void setParkingStrategy(ParkingStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        this.strategy.set(strategy);
    }

    public void setRateStrategy(Map<SpotSize, Double> rateMap) {
        if (rateMap == null) {
            throw new IllegalArgumentException("Rate map cannot be null");
        }
        this.rateMap.set(Map.copyOf(rateMap));
    }

    public void setFloors(List<ParkingFloor> floors) {
        if (floors == null) {
            throw new IllegalArgumentException("Floors cannot be null");
        }

        // Make defensive copy to prevent external modification
        List<ParkingFloor> floorsCopy = List.copyOf(floors);
        this.floors.set(floorsCopy);


        int smallCount = 0;
        int midCount = 0;
        int largeCount = 0;

        for (ParkingFloor floor : floorsCopy) {
            Map<SpotSize, List<ParkingSpot>> spots = floor.getSpots();
            if (spots != null) {
                smallCount += spots.getOrDefault(SpotSize.SMALL, Collections.emptyList()).size();
                midCount += spots.getOrDefault(SpotSize.MEDIUM, Collections.emptyList()).size();
                largeCount += spots.getOrDefault(SpotSize.LARGE, Collections.emptyList()).size();
            }
        }

        availableCounts.put(SpotSize.SMALL, smallCount);
        availableCounts.put(SpotSize.MEDIUM, midCount);
        availableCounts.put(SpotSize.LARGE, largeCount);
    }


    public Optional<ParkingTicket> park(Vehicle vehicle) {

        ParkingTicket newTicket = new ParkingTicket();

        ParkingTicket existingTicket = tickets.putIfAbsent(vehicle.getPlate(), newTicket);

        if (existingTicket != null) {
            throw new IllegalArgumentException("该车辆已停在车库，不可重复停车");
        }

        ParkingStrategy currentStrategy = strategy.get();
        if (currentStrategy == null) {
            tickets.remove(vehicle.getPlate());
            throw new IllegalStateException("Parking strategy not configured");
        }

        List<ParkingFloor> currentFloors = floors.get();
        Optional<ParkingSpot> spotOptional = currentStrategy.find(currentFloors, vehicle);
        if (spotOptional.isEmpty()) {
            tickets.remove(vehicle.getPlate());
            return Optional.empty();
        }

        ParkingSpot spot = spotOptional.get();

        Map<SpotSize, Double> currentRateMap = rateMap.get();
        Double rate = currentRateMap.get(spot.getSize());

        if (rate == null) {
            spot.release();
            tickets.remove(vehicle.getPlate());
            throw new IllegalStateException("No rate configured for spot size: " + spot.getSize());
        }

        newTicket.setRate(rate);
        newTicket.setStartTs(System.currentTimeMillis());
        newTicket.setVehicle(vehicle);
        newTicket.setSpot(spot);
        removeFromAvailableSpot(spot);
        return Optional.of(newTicket);
    }

    public double unpark(Vehicle vehicle) {

        ParkingTicket ticket = tickets.remove(vehicle.getPlate());

        if (ticket == null) {
            throw new IllegalArgumentException("该车辆未停在车库");
        }

        ticket.setEndTs(System.currentTimeMillis());

        ParkingSpot spot = ticket.getSpot();
        if (spot != null) {
            spot.release();// spot.release() 需要线程安全
            addToAvailableSpot(spot);
        }

        long duration = ticket.getEndTs() - ticket.getStartTs();
        return (double) duration / 1000 * ticket.getRate();//模拟
    }


    public void checkAvailableSpots() {
        String info = MessageFormat.format("剩余车位数：小型 {0} 个， 中型 {1} 个， 大型 {2} 个",
                availableCounts.getOrDefault(SpotSize.SMALL, 0),
                availableCounts.getOrDefault(SpotSize.MEDIUM, 0),
                availableCounts.getOrDefault(SpotSize.LARGE, 0));
        System.out.println(info);
    }

    private void removeFromAvailableSpot(ParkingSpot spot) {
        //lambda 表达式中的所有操作受锁保护
        availableCounts.compute(spot.getSize(), (key, old) -> {
            if (old == null || old == 0) {
                return 0;
            }
            return old - 1;
        });

    }

    private void addToAvailableSpot(ParkingSpot spot) {
        availableCounts.compute(spot.getSize(), (key, old) -> {
            if (old == null) {
                return 1;
            }
            return old + 1;
        });
    }
}
