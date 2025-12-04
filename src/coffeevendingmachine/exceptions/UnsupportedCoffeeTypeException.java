package coffeevendingmachine.exceptions;

import coffeevendingmachine.enums.CoffeeType;

public class UnsupportedCoffeeTypeException extends CoffeeMachineException {

    public UnsupportedCoffeeTypeException(CoffeeType type) {
        super("Unsupported Coffee Type: " + type.name());
    }
}
