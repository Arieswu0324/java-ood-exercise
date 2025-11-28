package vendingmachine.exception;

public class InvalidStateException extends VendingMachineException{
    public InvalidStateException(String state, String operation) {
        super("Cannot perform [" + operation +"] in the current ["+ state +"] state");
    }
}
