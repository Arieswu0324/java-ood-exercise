package ATMSystem.service;

import ATMSystem.enums.TransactionType;
import ATMSystem.entity.AccountInfo;
import ATMSystem.entity.CardInfo;
import ATMSystem.exceptions.ATMSystemException;
import ATMSystem.exceptions.InvalidAccountException;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;


//假设只能进行100元面值的交易
public class ATMInstance {

    //假设只有100元钞票的个数
    private static long CASH_100_VALUE_COUNT = 5000L;

    private CardInfo cardInfo;

    private AccountInfo accountInfo;

    private final BankService bankService = BankService.getInstance();

    private final ReentrantLock lock = new ReentrantLock();

    public ATMInstance() {
    }

    public void start() {
        cardInfo = null;
        accountInfo = null;
    }

    public void close() {
        cardInfo = null;
        accountInfo = null;
    }

    public void insertCard(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    public void validateCard(String PIN) {
        if (cardInfo == null) {
            throw new ATMSystemException("please insert the card first");
        }
        Optional<AccountInfo> accountInfo = bankService.validateAccount(cardInfo, PIN);
        if (accountInfo.isEmpty()) {
            throw new InvalidAccountException();
        }
        this.accountInfo = accountInfo.get();
    }

    public long enquireBalance() {
        return this.accountInfo.balance();
    }

    public long withdrawCash(long amount) {
        if (amount % 100 != 0) {
            throw new InvalidAccountException();
        }
        long count = amount / 100;


        lock.lock();
        try {
            AccountInfo updatedInfo = bankService.transact(accountInfo, amount, TransactionType.WITHDRAWAL);
            CASH_100_VALUE_COUNT -= count;
            this.accountInfo = updatedInfo;
        } catch (Exception e) {
            System.out.println("Exception happened, rolling back transaction");
            bankService.transact(accountInfo, amount, TransactionType.DEPOSIT);
            CASH_100_VALUE_COUNT += count;
            count = 0L;
        } finally {
            lock.unlock();

        }

        return count;
    }

    public long depositCash(long amount) {
        if (amount % 100 != 0) {
            throw new InvalidAccountException();
        }
        long count = amount / 100;

        lock.lock();
        try {
            AccountInfo updatedInfo = bankService.transact(accountInfo, amount, TransactionType.DEPOSIT);
            CASH_100_VALUE_COUNT += count;
            count = 0L;
            this.accountInfo = updatedInfo;
        } catch (Exception e) {
            System.out.println("Exception happened, rolling back transaction");
            bankService.transact(accountInfo, amount, TransactionType.WITHDRAWAL);
            CASH_100_VALUE_COUNT -= count;

        } finally {
            lock.unlock();
        }

        return count;
    }

    public CardInfo outputCard() {
        return cardInfo;
    }
}
