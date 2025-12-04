package coffeevendingmachine.state;

import coffeevendingmachine.CoffeeMachine;
import coffeevendingmachine.entity.CoffeeMachineStateContext;
import coffeevendingmachine.entity.CoffeeTransactionResult;
import coffeevendingmachine.enums.CoffeeType;
import coffeevendingmachine.enums.Coin;
import coffeevendingmachine.enums.Ingredient;
import coffeevendingmachine.exceptions.CannotMakeChangeException;
import coffeevendingmachine.exceptions.UnsupportedCoffeeTypeException;
import coffeevendingmachine.exceptions.UnsupportedStateException;

import java.util.Map;

public interface CoffeeMachineState {

    String OPERATION_DISPENSE = "DISPENSE";

    String OPERATION_REFILL = "refill";

    String OPERATION_COLLECT_MONEY = "collect money";


    CoffeeTransactionResult dispense(CoffeeMachineStateContext context, CoffeeType type, Map<Coin, Integer> payment) throws UnsupportedStateException, UnsupportedCoffeeTypeException, InterruptedException, CannotMakeChangeException;

    void refill(CoffeeMachineStateContext context, Ingredient ingredient) throws UnsupportedStateException;

    Map<Coin, Integer> collectMoney(CoffeeMachineStateContext context) throws UnsupportedStateException;


}
