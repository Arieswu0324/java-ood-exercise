package parkinglot;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * OOD - Parking Lot
 */
public class ParkingLotSystem {

    private List<ParkingFloor> floors;

    //concurrent
    private final Map<String, ParkingTicket> tickets = new ConcurrentHashMap<>();

    private ParkingStrategy strategy;

    private Map<SportSize, Double> rateMap;

    private static volatile ParkingLotSystem instance;

    //concurrent
    //floor, type, list of spots
    public static final Map<SportSize, Integer> availableCounts = new ConcurrentHashMap<>();

    ParkingLotSystem() {
    }

    public ParkingLotSystem getInstance() {
        if (instance != null) {
            synchronized (ParkingLotSystem.class) {
                if (instance != null) {
                    return new ParkingLotSystem();
                }
            }
        }
        return instance;
    }

    public void setParkingStrategy(ParkingStrategy strategy) {
        this.strategy = strategy;
    }

    public void setRateStrategy(Map<SportSize, Double> rateMap) {
        this.rateMap = rateMap;
    }

    public void setFloors(List<ParkingFloor> floors) {
        this.floors = floors;
        int smallCount = 0;
        int midCount = 0;
        int largeCount = 0;

        for (ParkingFloor floor : floors) {
            smallCount += floor.getSpots().get(SportSize.SMALL).size();
            midCount += floor.getSpots().get(SportSize.MEDIUM).size();
            largeCount += floor.getSpots().get(SportSize.LARGE).size();
        }

        availableCounts.put(SportSize.SMALL, smallCount);
        availableCounts.put(SportSize.MEDIUM, midCount);
        availableCounts.put(SportSize.LARGE, largeCount);
    }


    public ParkingTicket park(Vehicle vehicle) {
        if (tickets.containsKey(vehicle.plate)) {
            throw new IllegalArgumentException("该车辆已停在车库，不可重复停车");
        }
        ParkingSpot spot = strategy.find(floors, vehicle);
        if (spot != null) {
            spot.setOccupied(true);
            removeFromAvailableSpot(spot);
        } else {
            return null;
        }

        ParkingTicket ticket = new ParkingTicket();
        ticket.setRate(rateMap.get(spot.getSize()));
        ticket.setStartTs(System.currentTimeMillis());
        ticket.setVehicle(vehicle);

        spot.setTicket(ticket);
        tickets.put(vehicle.plate, ticket);


        return ticket;
    }

    private void removeFromAvailableSpot(ParkingSpot spot) {
        availableCounts.put(spot.getSize(), availableCounts.get(spot.getSize()) - 1);
    }


    public double unpark(Vehicle vehicle) {
        if (!tickets.containsKey(vehicle.plate)) {
            throw new IllegalArgumentException("该车辆未停在车库");
        }
        ParkingTicket ticket = tickets.get(vehicle.plate);
        ticket.setEndTs(System.currentTimeMillis());
        long duration = ticket.getEndTs() - ticket.getStartTs();
        return (double) duration / 1000 / 60 / 30 * ticket.getRate();
    }

    public void checkAvailableSpots() {
        String info = MessageFormat.format("剩余车位数：小型 {} 个， 中型 {} 个， 大型 {} 个",
                availableCounts.get(SportSize.SMALL), availableCounts.get(SportSize.MEDIUM), availableCounts.get(SportSize.LARGE));
        System.out.println(info);
    }
}
