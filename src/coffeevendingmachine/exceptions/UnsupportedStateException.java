package coffeevendingmachine.exceptions;

public class UnsupportedStateException extends CoffeeMachineException {
    public UnsupportedStateException(String operation, String state) {
        super("Unsupported operation " + operation + " at state " + state);
    }
}
