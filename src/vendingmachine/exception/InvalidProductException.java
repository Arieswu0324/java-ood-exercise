package vendingmachine.exception;

/**
 * Thrown when a product name is not recognized or not configured in the machine.
 */
public class InvalidProductException extends VendingMachineException {

    private final String productName;

    public InvalidProductException(String productName) {
        super("Unknown or invalid product: '" + productName + "'");
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }
}