package coffeevendingmachine.state;

import coffeevendingmachine.CoffeeMachine;
import coffeevendingmachine.entity.CoffeeMachineStateContext;
import coffeevendingmachine.entity.CoffeeTransactionResult;
import coffeevendingmachine.enums.CoffeeType;
import coffeevendingmachine.enums.Coin;
import coffeevendingmachine.enums.Ingredient;
import coffeevendingmachine.exceptions.UnsupportedStateException;

import java.util.Collections;
import java.util.Map;

public class MaintenanceState implements CoffeeMachineState {
    private static final String MAINTENANCE = "Maintenance";

    @Override
    public CoffeeTransactionResult dispense(CoffeeMachineStateContext context, CoffeeType type, Map<Coin, Integer> payment) throws UnsupportedStateException {
        throw new UnsupportedStateException(OPERATION_DISPENSE, MAINTENANCE);
    }

    @Override
    public void refill(CoffeeMachineStateContext context, Ingredient ingredient) throws UnsupportedStateException {
        context.inventory().put(ingredient, context.limit().get(ingredient));
    }

    @Override
    public Map<Coin, Integer> collectMoney(CoffeeMachineStateContext context) throws UnsupportedStateException {
        Map<Coin, Integer> collect = Collections.unmodifiableMap(context.money());
        context.money().clear();
        return collect;
    }
}
