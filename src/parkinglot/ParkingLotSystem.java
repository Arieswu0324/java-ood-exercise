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

    private final Map<SpotSize, Set<ParkingLotPushObserver>> pushObserverMap = new ConcurrentHashMap<>();

    private final Map<SpotSize, Set<ParkingLotPullObserver>> pullObserverMap = new ConcurrentHashMap<>();


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
            //发送通知
            SpotAvailableEvent event = new SpotAvailableEvent(spot);
            notifyObserversByPush(event);
            notifyObserversByPull(spot.getSize());
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

    public void subscribe(ParkingLotObserver observer) {
        if (observer instanceof ParkingLotPushObserver) {
            addToObserverMap(pushObserverMap, (ParkingLotPushObserver) observer);
        } else if (observer instanceof ParkingLotPullObserver) {
            addToObserverMap(pullObserverMap, (ParkingLotPullObserver) observer);
        } else {
            throw new IllegalArgumentException("observer not supported");
        }
    }

    public void unsubscribe(ParkingLotObserver observer) {
        if (observer instanceof ParkingLotPushObserver) {
            removeFromObserverMap(pushObserverMap, (ParkingLotPushObserver) observer);
        } else if (observer instanceof ParkingLotPullObserver) {
            removeFromObserverMap(pullObserverMap, (ParkingLotPullObserver) observer);

        } else {
            throw new IllegalArgumentException("observer not supported");
        }
    }

    private <T extends ParkingLotObserver> void addToObserverMap(Map<SpotSize, Set<T>> observerMap, T observer) {
        observer.getInterestedSpot()
                .forEach(size -> observerMap.computeIfAbsent(size, k -> ConcurrentHashMap.newKeySet()).add(observer));
    }

    private <T extends ParkingLotObserver> void removeFromObserverMap(Map<SpotSize, Set<T>> observerMap, T observer) {
        observer.getInterestedSpot()
                .forEach(size -> observerMap.computeIfPresent(size, (key, set) -> {
                    set.remove(observer);
                    return set.isEmpty() ? null : set;  // 返回 null 时会移除 key
                }));
    }

    private void notifyObserversByPush(SpotAvailableEvent event) {
        SpotSize size = event.getSpot().getSize();
        pushObserverMap.getOrDefault(size, Set.of()).forEach(observer -> observer.onAvailableSpot(event));
    }

    private void notifyObserversByPull(SpotSize size) {
        pullObserverMap.getOrDefault(size, Set.of()).forEach(observer -> observer.onAvailableSpot(this));

    }

    public Optional<ParkingSpot> getAvailableSpot(SpotSize size) {
        //对每一层轮询，找到第一个符合的空位
        for (ParkingFloor floor : floors.get()) {
            List<ParkingSpot> spots = floor.getSpots().getOrDefault(size, Collections.emptyList());
            for (ParkingSpot spot : spots) {
                if (!spot.isOccupied()) {//time to check
                    return Optional.of(spot);//time to use
                }
            }
        }
        return Optional.empty();

    }

}
