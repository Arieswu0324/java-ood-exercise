package ATMSystem;

import ATMSystem.entity.AccountInfo;
import ATMSystem.entity.CardInfo;
import ATMSystem.service.ATMInstance;
import ATMSystem.service.BankService;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        //Preparation
        ATMInstance instance = getAtmInstance();
        System.out.println("card validated");

        System.out.println("balance: " + instance.enquireBalance());

        long amount = instance.withdrawCash(1000);
        System.out.println("cash withdrawn: " + amount);

        amount = instance.depositCash(100);
        System.out.println("cash deposited: " + amount);

        System.out.println("balance: " + instance.enquireBalance());


    }

    private static ATMInstance getAtmInstance() {
        BankService bankService = BankService.getInstance();
        AccountInfo accountInfo = bankService.openAnAccount("Joe Doe");
        System.out.println(accountInfo.toString());
        Optional<CardInfo> cardInfo = bankService.issueCard(accountInfo.accountNumber());
        cardInfo.ifPresent(info -> bankService.setPIN(info, "1234", "2345"));
        CardInfo myCard = cardInfo.get();
        System.out.println(myCard.toString());


        //Using ATM
        ATMInstance instance = new ATMInstance();

        instance.start();


        instance.insertCard(myCard);
        instance.validateCard("2345");
        return instance;
    }
}
