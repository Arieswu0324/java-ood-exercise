package ATMSystem.entity;

import java.time.LocalDate;

public record CardInfo(String cardId, String cardHolderName, LocalDate expiryDate) {
}
