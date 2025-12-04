package coffeevendingmachine.exceptions;

public class CannotMakeChangeException extends CoffeeMachineException{
    public CannotMakeChangeException() {
        super("Cannot make change!");
    }
}
