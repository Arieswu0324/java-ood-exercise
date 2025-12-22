package hotelmanagementsystem.entity;

import hotelmanagementsystem.enums.PaymentMethod;
import hotelmanagementsystem.enums.PaymentStatus;
import hotelmanagementsystem.exception.HotelManagementException;

import java.time.LocalDateTime;

public class PaymentInfo {
    private final PaymentMethod method;
    private PaymentStatus status;

    private long amount;

    private LocalDateTime paidTime;

    private String onlineAccount;

    private CardInfo cardInfo;

    private PaymentInfo(PaymentMethod method, CardInfo cardInfo, String onlineAccount) {
        this.method = method;
        this.cardInfo = cardInfo;
        this.onlineAccount = onlineAccount;
    }

    public void execute(long price, long nights) {
        this.amount = price * nights;
        switch (method) {
            case CASH:
                System.out.println("cash paid");
                break;
            case CREDIT_CARD:
                System.out.println("paid by CC");
                break;
            case ONLINE_PAYMENT:
                System.out.println("paid by online payment");
                break;
            default:
                throw new HotelManagementException("invalid payment method");
        }

        status = PaymentStatus.COMPLETE;
    }

    public static class Builder {
        private final PaymentMethod method;

        private String onlineAccount = null;

        private CardInfo cardInfo = null;


        public Builder(PaymentMethod method) {
            this.method = method;
        }

        public Builder withCard(CardInfo cardInfo) {
            this.cardInfo = cardInfo;
            return this;
        }

        public Builder withOnline(String account) {
            this.onlineAccount = account;
            return this;
        }

        public PaymentInfo build() {
            //validations
            return new PaymentInfo(method, cardInfo, onlineAccount);
        }
    }
}
