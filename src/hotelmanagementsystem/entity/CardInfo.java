package hotelmanagementsystem.entity;

import java.time.LocalDate;

public record CardInfo(String cardNo, String cardHolder, LocalDate expiry, String cvv) {
}
