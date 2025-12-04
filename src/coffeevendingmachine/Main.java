package coffeevendingmachine;

import coffeevendingmachine.entity.CoffeeTransactionResult;
import coffeevendingmachine.enums.CoffeeType;
import coffeevendingmachine.enums.Coin;
import coffeevendingmachine.enums.Ingredient;
import coffeevendingmachine.exceptions.CoffeeMachineException;
import coffeevendingmachine.observer.MessageObserver;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws CoffeeMachineException, InterruptedException {
        Map<CoffeeType, Integer> price = new HashMap<>();
        price.put(CoffeeType.ESPRESSO, 15);
        price.put(CoffeeType.LATTE, 25);
        price.put(CoffeeType.CAPPUCCINO, 30);

        Map<Ingredient, Integer> limit = new HashMap<>();
        limit.put(Ingredient.BEAN, 100);
        limit.put(Ingredient.MILK, 100);
        limit.put(Ingredient.CREAM, 50);
        limit.put(Ingredient.SUGAR, 10);
        CoffeeMachine machine = new CoffeeMachine(price, limit);

        machine.initialize();

        MessageObserver observer = new MessageObserver();
        machine.addNotificationObserver(observer);


        machine.start();

        Map<CoffeeType, Integer> menu = machine.getMenu();
        menu.forEach((k, v) ->
                System.out.println("type" + k + ": price: " + v)
        );

        Map<Coin, Integer> intake = new HashMap<>();
        intake.put(Coin.YUAN_1, 1);
        intake.put(Coin.JIAO_1, 5);
        CoffeeTransactionResult result = machine.dispense(CoffeeType.ESPRESSO, intake);
        System.out.println(result.toString());

        machine.maintenance();
        machine.refill(Ingredient.BEAN);


    }
}
