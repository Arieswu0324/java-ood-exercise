package vendingmachine.exception;

/**
 * Thrown when payment is invalid (null, empty, or contains invalid denominations).
 */
public class InvalidPaymentException extends VendingMachineException {

    public InvalidPaymentException(String message) {
        super(message);
    }
}