package vendingmachine.exception;

/**
 * Thrown when the machine cannot provide exact change with available denominations.
 */
public class InsufficientChangeException extends VendingMachineException {

    private final int changeRequired;

    public InsufficientChangeException(int changeRequired) {
        super(String.format("Unable to provide change of %d pence with available denominations. " +
            "Transaction cancelled and payment returned.", changeRequired));
        this.changeRequired = changeRequired;
    }

    public int getChangeRequired() {
        return changeRequired;
    }
}