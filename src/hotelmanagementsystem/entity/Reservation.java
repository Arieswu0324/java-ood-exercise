package hotelmanagementsystem.entity;

import hotelmanagementsystem.enums.ReservationStatus;
import hotelmanagementsystem.enums.RoomType;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reservation {

    private final String id;

    private final RoomType type;
    private List<String> roomNumbers;
    private final User reserver;
    private final List<User> guests;
    private LocalDateTime checkIn;
    private LocalDateTime checkout;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final long price;
    private ReservationStatus status;
    private int roomCount;

    private PaymentInfo paymentInfo;

    private Reservation(RoomType type, int roomCount, User reserver, LocalDate startDate, LocalDate endDate, long price) {
        //validations
        this.type = type;
        this.id = UUID.randomUUID().toString();
        this.reserver = reserver;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.guests = new ArrayList<>();
        this.status = ReservationStatus.SUCCESS;
        this.roomCount = roomCount;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public void setGuests(List<User> guests) {
        this.guests.addAll(guests);
    }

    public void bill() {
        long nights = ChronoUnit.DAYS.between(checkIn, checkout);
        try {
            this.paymentInfo.execute(price, nights);
        } catch (Exception e) {
            System.out.println("payment unsuccessful, please retry");
            throw e;
        }
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setRoomNumbers(List<String> roomNumbers) {
        this.roomNumbers = roomNumbers;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public void setCheckout(LocalDateTime checkout) {
        this.checkout = checkout;
    }

    public RoomType getType() {
        return type;
    }

    public static class Builder {
        private RoomType type;
        private User reserver;
        private LocalDate startDate;
        private LocalDate endDate;
        private long price;
        private int roomCount = 1;

        public Builder() {
        }

        public Builder withType(RoomType type) {
            this.type = type;
            return this;
        }

        public Builder withCount(int count) {
            this.roomCount = count;
            return this;
        }

        public Builder by(User reserver) {
            this.reserver = reserver;
            return this;
        }

        public Builder startFrom(LocalDate from) {
            this.startDate = from;
            return this;
        }

        public Builder endTo(LocalDate to) {
            this.endDate = to;
            return this;
        }

        public Builder withPrice(long price) {
            this.price = price;
            return this;
        }

        public Reservation build() {
            //validations
            return new Reservation(type, roomCount, reserver, startDate, endDate, price);
        }
    }

}
