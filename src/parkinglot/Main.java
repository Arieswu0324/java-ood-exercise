package parkinglot;

import parkinglot.entity.ParkingFloor;
import parkinglot.entity.ParkingSpot;
import parkinglot.entity.ParkingTicket;
import parkinglot.entity.Vehicle;
import parkinglot.enums.SpotSize;
import parkinglot.factory.TruckFactory;
import parkinglot.observers.CarPushObserver;
import parkinglot.observers.MotorPullObserver;
import parkinglot.observers.TruckPushObserver;
import parkinglot.strategy.NearFirstStrategy;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ParkingLotSystem parkingLotSystem = ParkingLotSystem.getInstance();
        Map<SpotSize, List<ParkingSpot>> map = new HashMap<>();
        ParkingSpot spot = new ParkingSpot(1, SpotSize.LARGE);
        map.put(SpotSize.LARGE, List.of(spot));

        ParkingFloor floor1 = new ParkingFloor(1);
        floor1.setSpots(map);
        parkingLotSystem.setFloors(List.of(floor1));

        Map<SpotSize, Double> rateStrategy = new HashMap<>();
        rateStrategy.put(SpotSize.LARGE, 0.5);
        parkingLotSystem.setRateStrategy(rateStrategy);

        parkingLotSystem.setParkingStrategy(new NearFirstStrategy());

        //测试消息通知
        CarPushObserver carObserver = new CarPushObserver("AAB CCD");
        TruckPushObserver truckObserver = new TruckPushObserver("TRU CKA");
        MotorPullObserver motorObserver = new MotorPullObserver("MOT ORC");

        parkingLotSystem.subscribe(carObserver);
        parkingLotSystem.subscribe(truckObserver);
        parkingLotSystem.subscribe(motorObserver);


        TruckFactory factory = TruckFactory.getInstance();
        Vehicle myTruck = factory.createVehicle("ABC CDE");

        parkingLotSystem.checkAvailableSpots();

        Optional<ParkingTicket> ticketOptional = parkingLotSystem.park(myTruck);
        if (ticketOptional.isEmpty()) {
            System.out.println("No available spot to park, subscribed to available spots");
        } else {
            ParkingTicket ticket = ticketOptional.get();
            Instant instance = Instant.ofEpochMilli(ticket.getStartTs());
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instance, ZoneId.of("Asia/Shanghai"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");

            String formattedTime = zonedDateTime.format(formatter);


            String message = MessageFormat.format("停入车库，开始时间{0}, 费率：{1}", formattedTime, ticket.getRate());
            System.out.println(message);
        }

        //注意，这个时间实际上大于1000，还有系统时间消耗
        Thread.sleep(1000);

        double expense = parkingLotSystem.unpark(myTruck);
        System.out.printf("my parking fee: %.2f", expense);

        parkingLotSystem.unsubscribe(carObserver);
        parkingLotSystem.unsubscribe(truckObserver);
        parkingLotSystem.unsubscribe(motorObserver);

    }
}
