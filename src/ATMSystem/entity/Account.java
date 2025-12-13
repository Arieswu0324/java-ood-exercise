package ATMSystem.entity;

import ATMSystem.exceptions.InsufficientFundException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private final String accountNumber;
    private final String userName;
    private final AtomicLong balance;
    private final Map<String, Card> cards;
    private final ReentrantLock lock;

    public Account(String userName) {
        this.userName = userName;
        this.accountNumber = UUID.randomUUID().toString();
        this.balance = new AtomicLong(0L);
        this.cards = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();

    }

    public String getAccountNumber() {
        return accountNumber;
    }


    public String getUserName() {
        return userName;
    }

    public long getBalance() {
        return balance.get();
    }

    public void updateBalance(long difference) {
        lock.lock();
        try {
            if (difference < 0 && balance.addAndGet(difference) < 0) {
                throw new InsufficientFundException();
            }
            balance.addAndGet(difference);
        } finally {
            lock.unlock();
        }
    }

    public void addCard(Card card) {
        cards.put(card.getCardId(), card);
    }

    public Card getCard(String cardId) {
        return cards.get(cardId);
    }
}
