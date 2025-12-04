package coffeevendingmachine.state;

import coffeevendingmachine.entity.Coffee;
import coffeevendingmachine.entity.CoffeeMachineStateContext;
import coffeevendingmachine.entity.CoffeeTransactionResult;
import coffeevendingmachine.enums.CoffeeType;
import coffeevendingmachine.enums.Coin;
import coffeevendingmachine.enums.Ingredient;
import coffeevendingmachine.exceptions.CannotMakeChangeException;
import coffeevendingmachine.exceptions.UnsupportedCoffeeTypeException;
import coffeevendingmachine.exceptions.UnsupportedStateException;

import java.util.HashMap;
import java.util.Map;

public class IdleState implements CoffeeMachineState {
    private static final String IDLE = "idle";

    @Override
    public CoffeeTransactionResult dispense(CoffeeMachineStateContext context, CoffeeType type, Map<Coin, Integer> payment) throws UnsupportedCoffeeTypeException, InterruptedException, CannotMakeChangeException {
        //TODO: 这个方法缺少逻辑 1. 找零校验，小于0抛出异常 2. make coffee之后扣减 inventory 3. 收的钱没有增加money
        int price = context.menu().get(type);
        int input = computeFund(payment);
        int output = input - price;

        Map<Coin, Integer> moneySnapshot = new HashMap<>(context.money());
        Map<Coin, Integer> change = context.strategy().findChange(output, moneySnapshot);

        //execute transaction
        Coffee coffee = context.factory().make(type);
        doFindChange(change, context.money());
        return new CoffeeTransactionResult(coffee, change);
    }

    private int computeFund(Map<Coin, Integer> payment) {

        return payment.entrySet().stream().mapToInt(entry -> entry.getKey().getD() * entry.getValue()).sum();
    }

    @Override
    public void refill(CoffeeMachineStateContext context, Ingredient ingredient) throws UnsupportedStateException {
        throw new UnsupportedStateException(OPERATION_REFILL, IDLE);
    }

    @Override
    public Map<Coin, Integer> collectMoney(CoffeeMachineStateContext context) throws UnsupportedStateException {
        throw new UnsupportedStateException(OPERATION_COLLECT_MONEY, IDLE);
    }

    private void doFindChange(Map<Coin, Integer> change, Map<Coin, Integer> money) {
        change.forEach((key, value) -> {
            money.compute(key, (k, v) -> {
                v = money.get(key) - value;
                return v;
            });
        });
    }
}
