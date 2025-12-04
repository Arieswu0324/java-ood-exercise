package coffeevendingmachine.state;

import coffeevendingmachine.CoffeeMachine;
import coffeevendingmachine.entity.CoffeeMachineStateContext;
import coffeevendingmachine.entity.CoffeeTransactionResult;
import coffeevendingmachine.enums.CoffeeType;
import coffeevendingmachine.enums.Coin;
import coffeevendingmachine.enums.Ingredient;
import coffeevendingmachine.exceptions.UnsupportedCoffeeTypeException;
import coffeevendingmachine.exceptions.UnsupportedStateException;

import java.util.Map;

public class OutOfServiceState implements CoffeeMachineState {

    private static final String OUT_OF_SERVICE = "Out Of Service";

    @Override
    public CoffeeTransactionResult dispense(CoffeeMachineStateContext context, CoffeeType type, Map<Coin, Integer> payment) throws UnsupportedStateException, UnsupportedCoffeeTypeException, InterruptedException {
        throw new UnsupportedStateException(OPERATION_DISPENSE, OUT_OF_SERVICE);
    }

    @Override
    public void refill(CoffeeMachineStateContext context, Ingredient ingredient) throws UnsupportedStateException {
        throw new UnsupportedStateException(OPERATION_REFILL, OUT_OF_SERVICE);
    }

    @Override
    public Map<Coin, Integer> collectMoney(CoffeeMachineStateContext context) throws UnsupportedStateException {
        throw new UnsupportedStateException(OPERATION_COLLECT_MONEY, OUT_OF_SERVICE);
    }
}
