package ATMSystem.service;

import ATMSystem.enums.TransactionType;
import ATMSystem.entity.AccountInfo;
import ATMSystem.entity.CardInfo;
import ATMSystem.exceptions.ATMSystemException;
import ATMSystem.exceptions.InvalidAccountException;
import ATMSystem.exceptions.InvalidAmountException;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;


//假设只能进行100元面值的交易
public class ATMInstance {

    //假设只有100元钞票的个数
    //简单计数器可以用Atomic，底层是CAS，效率高
    private final AtomicLong CASH_100_VALUE_COUNT = new AtomicLong(5000);

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
        if (accountInfo == null) {
            throw new InvalidAccountException();
        }
        return this.accountInfo.balance();
    }

    public long withdrawCash(long amount) {
        if (amount % 100 != 0) {
            throw new InvalidAmountException();
        }
        long count = amount / 100;

        //CAS操作
        long currentCash;
        do {
            currentCash = CASH_100_VALUE_COUNT.get();
            if (currentCash < count) {//只是检查，不需要回滚
                throw new ATMSystemException("ATM 现金不足");
            }
            // check - and - act
        } while (!CASH_100_VALUE_COUNT.compareAndSet(currentCash, currentCash - count));
        //出CAS代表已经扣款

        try {
            //然后账号扣款
            AccountInfo updatedInfo = bankService.transact(accountInfo, amount, TransactionType.WITHDRAWAL);
            if (updatedInfo == null) {//这里没有扣款，transact不需要回滚
                throw new InvalidAccountException();
            }
            //这里代表已经账户扣款
            this.accountInfo = updatedInfo;

            return count;
        } catch (Exception e) {
            //TODO 这里有个问题，就是如果银行交易成功了，但是因为网络原因返回时报网络异常，
            // 此时对于本地来说是UNKNOWN状态，并不知道transact是否成功，这里没有回滚，就有一致性问题。
            // 需要进一步进行“reversal”操作，也就是说不管成没成功，取消交易，远程回滚，本地也回滚。
            // 此时的回滚需要带版本号，否则银行系统不知道回滚哪一笔交易
            CASH_100_VALUE_COUNT.addAndGet(count);

            // 重新抛出异常通知上层
            throw e;
        }

    }

    public long depositCash(long amount) {
        if (amount % 100 != 0) {
            throw new InvalidAmountException();
        }
        long count = amount / 100;


        CASH_100_VALUE_COUNT.addAndGet(count);

        try {
            AccountInfo updatedInfo = bankService.transact(accountInfo, amount, TransactionType.DEPOSIT);
            if (updatedInfo == null) {//这里不需要回滚，此时未进行交易
                throw new InvalidAccountException();
            }

            this.accountInfo = updatedInfo;
            return count;

        } catch (Exception e) {

            CASH_100_VALUE_COUNT.addAndGet(-count);//回滚情况上同
            throw e;
        }

    }

    public void outputCard() {
        System.out.println("card returned");
    }
}
