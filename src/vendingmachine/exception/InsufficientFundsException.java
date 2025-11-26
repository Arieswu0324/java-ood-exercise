package vendingmachine.exception;

/**
 * Thrown when customer doesn't provide enough money for the selected product.
 */
public class InsufficientFundsException extends VendingMachineException {

    private final int required;
    private final int provided;

    public InsufficientFundsException(int required, int provided) {
        super(String.format("Insufficient funds: required %d pence, but only %d pence provided",
            required, provided));
        this.required = required;
        this.provided = provided;
    }

    public int getRequired() {
        return required;
    }

    public int getProvided() {
        return provided;
    }

    public int getShortfall() {
        return required - provided;
    }
}