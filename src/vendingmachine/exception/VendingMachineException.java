package vendingmachine.exception;

/**
 * Base exception for all vending machine related errors.
 * This allows callers to catch all vending machine exceptions with a single catch block if needed.
 */
public class VendingMachineException extends Exception {

    public VendingMachineException(String message) {
        super(message);
    }

    public VendingMachineException(String message, Throwable cause) {
        super(message, cause);
    }
}