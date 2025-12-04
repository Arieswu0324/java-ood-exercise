package coffeevendingmachine.exceptions;

public class CoffeeMachineException extends Exception {
    public CoffeeMachineException(String message) {
        super(message);
    }

    public CoffeeMachineException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
