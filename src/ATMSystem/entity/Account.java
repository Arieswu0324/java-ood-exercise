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
        lock = new ReentrantLock();

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

    public ReentrantLock getLock() {
        return this.lock;
    }

    //对于单纯的计数器，并发安全可以用CAS实现，比锁效率高
    public void updateBalance(long difference) {
        long currentBalance;
        long newBalance;
        do {
            currentBalance = this.balance.get();
            newBalance = currentBalance + difference;
            if (newBalance < 0) {
                throw new InsufficientFundException();
            }

        } while (balance.compareAndSet(currentBalance, newBalance));


    }


    public void addCard(Card card) {
        cards.put(card.getCardId(), card);
    }

    public Card getCard(String cardId) {
        return cards.get(cardId);
    }
}
