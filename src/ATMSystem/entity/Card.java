package ATMSystem.entity;

import ATMSystem.exceptions.InvalidCardException;
import ATMSystem.exceptions.InvalidPINException;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class Card {
    private final String accountNumber;
    private final String cardId;
    private final String cardHolderName;
    private final LocalDate expiryDate;
    private String PIN;
    private final ReentrantLock lock = new ReentrantLock();


    public Card(String accountNumber, String userName) {
        this.accountNumber = accountNumber;
        this.cardId = UUID.randomUUID().toString();
        this.cardHolderName = userName;
        this.expiryDate = LocalDate.now().plusYears(5);
        this.PIN = "1234";
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getCardId() {
        return cardId;
    }

    public boolean authenticate(CardInfo insertedInfo, String enteredPIN) {
        if (!cardId.equals(insertedInfo.cardId())) {
            throw new InvalidCardException("invalid card number");
        }

        if (!cardHolderName.equals(insertedInfo.cardHolderName())) {
            throw new InvalidCardException("invalid card holder name");
        }

        if (!expiryDate.isAfter(LocalDate.now())) {
            throw new InvalidCardException("Card expired");
        }

        lock.lock();
        try {
            if (!PIN.equals(enteredPIN)) {
                throw new InvalidPINException();
            }
        } finally {
            lock.unlock();
        }

        return true;
    }

    public void setPIN(String oldPIN, String newPIN) {
        lock.lock();
        try {
            if (!oldPIN.equals(PIN)) {
                throw new InvalidPINException();
            }
            PIN = newPIN;
        } finally {
            lock.unlock();
        }
    }
}
