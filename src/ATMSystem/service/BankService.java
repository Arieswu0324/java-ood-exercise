package ATMSystem.service;

import ATMSystem.enums.TransactionType;
import ATMSystem.entity.Account;
import ATMSystem.entity.AccountInfo;
import ATMSystem.entity.Card;
import ATMSystem.entity.CardInfo;
import ATMSystem.exceptions.InvalidAccountException;
import ATMSystem.exceptions.InvalidCardException;
import ATMSystem.exceptions.InvalidPINException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class BankService {

    private final Map<String, Account> accountMap = new ConcurrentHashMap<>();

    private final Map<String, String> cardToAccountMap = new ConcurrentHashMap<>();

    private static final BankService INSTANCE = new BankService();

    private BankService() {
    }

    public static BankService getInstance() {
        return INSTANCE;
    }


    public Optional<AccountInfo> validateAccount(CardInfo cardInfo, String PIN) {
        String accountNo = cardToAccountMap.get(cardInfo.cardId());
        if (accountNo == null) {
            return Optional.empty();
        }

        Account account = accountMap.get(accountNo);
        if (account == null) {
            return Optional.empty();
        }

        Card card = account.getCard(cardInfo.cardId());
        if (card == null) {
            return Optional.empty();
        }

        if (card.authenticate(cardInfo, PIN)) {
            AccountInfo accountInfo = new AccountInfo(
                    account.getAccountNumber(),
                    account.getUserName(),
                    account.getBalance()
            );
            return Optional.of(accountInfo);
        }

        return Optional.empty();
    }

    public Optional<CardInfo> issueCard(String accountNumber) {
        Account account = accountMap.get(accountNumber);
        if (account == null) {
            throw new InvalidAccountException();
        }

        //确保只有对同一个 Account 实例进行操作的线程才会被阻塞，不同 Account 之间的操作可以并行。
        synchronized (account) {
            Card card = new Card(accountNumber, account.getUserName());
            account.addCard(card);
            cardToAccountMap.put(card.getCardId(), accountNumber);

            CardInfo cardInfo = new CardInfo(
                    card.getCardId(),
                    card.getCardHolderName(),
                    card.getExpiryDate()
            );
            return Optional.of(cardInfo);
        }
    }

    public AccountInfo openAnAccount(String accountName) {
        Account account = new Account(accountName);
        accountMap.put(account.getAccountNumber(), account);
        return new AccountInfo(account.getAccountNumber(),
                account.getUserName(),
                account.getBalance());
    }

    public AccountInfo transact(AccountInfo accountInfo, long amount, TransactionType type) {
        Account account = accountMap.get(accountInfo.accountNumber());

        if (account == null) {
            return null;
        }
        //这里，内部updateBalance使用了CAS，这里的同步锁保证update和return的原子性
        //即return的一定是update之后的快照，如果不加锁，那么return的是最终一致性，
        //如果update和return之间有其他线程进行了update，则返回的是最终结果。要怎么处理取决于业务要求
        ReentrantLock lock = account.getLock();
        lock.lock();
        try {
            if (TransactionType.WITHDRAWAL.equals(type)) {
                account.updateBalance(-amount);
            } else if (TransactionType.DEPOSIT.equals(type)) {
                account.updateBalance(amount);
            }

            return new AccountInfo(account.getAccountNumber(), account.getUserName(), account.getBalance());
        } finally {
            lock.unlock();
        }
    }

    public void setPIN(CardInfo cardInfo, String oldPIN, String newPIN) {
        String accountNo = cardToAccountMap.get(cardInfo.cardId());
        if (accountNo == null) {
            throw new InvalidCardException();
        }

        Account account = accountMap.get(accountNo);
        if (account == null) {
            throw new InvalidAccountException();
        }

        Card card = account.getCard(cardInfo.cardId());
        if (card == null) {
            throw new InvalidCardException();
        }

        if (!card.authenticate(cardInfo, oldPIN)) {
            throw new InvalidPINException();
        }

        card.setPIN(oldPIN, newPIN);
    }

}
