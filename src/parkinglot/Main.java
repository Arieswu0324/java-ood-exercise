package parkinglot;

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
        ParkingSpot spot = new ParkingSpot(1, SpotSize.SMALL);
        map.put(SpotSize.SMALL, List.of(spot));

        ParkingFloor floor1 = new ParkingFloor(1);
        floor1.setSpots(map);
        parkingLotSystem.setFloors(List.of(floor1));

        Map<SpotSize, Double> rateStrategy = new HashMap<>();
        rateStrategy.put(SpotSize.SMALL, 0.5);
        parkingLotSystem.setRateStrategy(rateStrategy);

        parkingLotSystem.setParkingStrategy(new NearFirstStrategy());

        //测试消息通知
        CarObserver carObserver = new CarObserver("AAB CCD");
        TruckObserver truckObserver = new TruckObserver("TRU CKA");

        parkingLotSystem.subscribe(carObserver);
        parkingLotSystem.subscribe(truckObserver);


        CarFactory factory = CarFactory.getInstance();
        Vehicle myCar = factory.create("ABC CDE");

        parkingLotSystem.checkAvailableSpots();

        Optional<ParkingTicket> ticketOptional = parkingLotSystem.park(myCar);
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

        double expense = parkingLotSystem.unpark(myCar);
        System.out.printf("my parking fee: %.2f", expense);

        parkingLotSystem.unsubscribe(carObserver);
        parkingLotSystem.unsubscribe(truckObserver);

    }
}
