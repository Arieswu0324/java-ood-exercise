package ATMSystem.service;

import ATMSystem.enums.TransactionType;
import ATMSystem.entity.Account;
import ATMSystem.entity.AccountInfo;
import ATMSystem.entity.Card;
import ATMSystem.entity.CardInfo;
import ATMSystem.exceptions.InvalidCardException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

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
        AtomicReference<Optional<AccountInfo>> accountInfoOptional = new AtomicReference<>(Optional.empty());
        cardToAccountMap.compute(cardInfo.cardId(), (k, v) -> {
            if (cardToAccountMap.containsKey(cardInfo.cardId())) {
                String accountNo = cardToAccountMap.get(cardInfo.cardId());
                if (accountMap.containsKey(accountNo)) {
                    Account account = accountMap.get(accountNo);
                    Card card = account.getCard(cardInfo.cardId());
                    if (card != null && card.authenticate(cardInfo, PIN)) {
                        AccountInfo accountInfo = new AccountInfo(account.getAccountNumber(), account.getUserName(), account.getBalance());
                        accountInfoOptional.set(Optional.of(accountInfo));
                    }
                }
            }
            return k;
        });
        return accountInfoOptional.get();

    }

    public Optional<CardInfo> issueCard(String accountNumber) {
        AtomicReference<Optional<CardInfo>> cardOptional = new AtomicReference<>(Optional.empty());
        accountMap.computeIfPresent(accountNumber, (k, v) -> {
            Account account = accountMap.get(accountNumber);
            Card card = new Card(accountNumber, account.getUserName());
            account.addCard(card);
            CardInfo cardInfo = new CardInfo(card.getCardId(), card.getCardHolderName(), card.getExpiryDate());
            cardOptional.set(Optional.of(cardInfo));
            cardToAccountMap.put(card.getCardId(), accountNumber);
            return account;
        });
        return cardOptional.get();
    }

    public AccountInfo openAnAccount(String accountName) {
        Account account = new Account(accountName);
        accountMap.put(account.getAccountNumber(), account);
        return new AccountInfo(account.getAccountNumber(),
                account.getUserName(),
                account.getBalance());
    }

    public AccountInfo transact(AccountInfo accountInfo, long amount, TransactionType type) {
        AtomicReference<AccountInfo> newAccountInfo = new AtomicReference<>();
        accountMap.computeIfPresent(accountInfo.accountNumber(), (k, v) -> {
            Account account = accountMap.get(accountInfo.accountNumber());
            if (TransactionType.WITHDRAWAL.equals(type)) {
                account.updateBalance(-amount);
            } else if (TransactionType.DEPOSIT.equals(type)) {
                account.updateBalance(amount);
            }
            AccountInfo newInfo = new AccountInfo(accountInfo.accountNumber(), accountInfo.accountName(), account.getBalance());
            newAccountInfo.set(newInfo);
            return account;
        });

        return newAccountInfo.get();
    }

    public void setPIN(CardInfo cardInfo, String oldPIN, String newPIN) {
        cardToAccountMap.computeIfPresent(cardInfo.cardId(), (k, v) -> {
            Account account = accountMap.get(cardToAccountMap.get(cardInfo.cardId()));
            Card card = account.getCard(cardInfo.cardId());
            if(card == null){
                throw new InvalidCardException();
            }
            card.authenticate(cardInfo, oldPIN);
            card.setPIN(oldPIN, newPIN);

            return v;
        });
    }

}
