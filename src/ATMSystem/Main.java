package ATMSystem;

import ATMSystem.entity.AccountInfo;
import ATMSystem.entity.CardInfo;
import ATMSystem.service.ATMInstance;
import ATMSystem.service.BankService;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        ATMInstance instance = getAtmInstance();
        System.out.println("card validated");

        System.out.println("balance: " + instance.enquireBalance());

        long amount = instance.withdrawCash(1000);
        System.out.println("cash withdrawn: " + amount);

        amount = instance.depositCash(100);
        System.out.println("cash deposited: " + amount);

        System.out.println("balance: " + instance.enquireBalance());

        instance.outputCard();

        instance.close();

        //test
        Map<String, List<Integer>> map = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        map.put("a",list);
        map.computeIfAbsent("b", k->new ArrayList<>()).add(1);
        List<Integer> res = map.get("b");
        System.out.println(res.stream().map(String::valueOf).toString());

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
