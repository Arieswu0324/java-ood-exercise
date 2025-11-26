package vendingmachine.exception;

/**
 * Thrown when a requested product is out of stock.
 */
public class OutOfStockException extends VendingMachineException {

    private final String productName;

    public OutOfStockException(String productName) {
        super("Product '" + productName + "' is out of stock");
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }
}