package vendingmachine.state;

import vendingmachine.service.StateContext;
import vendingmachine.service.VendingMachine;
import vendingmachine.constants.VendingMachineOperationConstants;
import vendingmachine.entity.Product;
import vendingmachine.entity.TransactionResult;
import vendingmachine.enums.Money;
import vendingmachine.enums.State;
import vendingmachine.exception.InvalidStateException;
import vendingmachine.exception.VendingMachineException;

import java.util.*;

public class MaintenanceState implements VendingMachineState {
    @Override
    public TransactionResult dispense(StateContext context, String product, Map<Money, Integer> intake) throws VendingMachineException {
        throw new InvalidStateException(getStateName(), VendingMachineOperationConstants.DISPENSE);
    }

    @Override
    public Map<Money, Integer> collectMoney(StateContext context) {
        TreeMap<Money, Integer> funds = context.funds();
        Map<Money, Integer> fundCopy = createFundSnapshot(funds);
        funds.clear();
        return fundCopy;
    }

    @Override
    public List<Product> refill(StateContext context, List<Product> products) {
        Map<String, Integer> quantities = context.quantities();
        Map<String, List<Product>> stocking = context.stocking();


        List<Product> returnedProducts = new ArrayList<>();

        for (Product product : products) {
            String productName = product.getName();

            if (!quantities.containsKey(productName)) {
                returnedProducts.add(product); // Unknown product type
                continue;
            }

            // Get capacity and current stock
            int capacity = quantities.get(productName);
            List<Product> currentStock = stocking.get(productName);
            int currentSize = (currentStock != null) ? currentStock.size() : 0;

            // Add if capacity allows
            if (currentSize < capacity) {
                stocking.computeIfAbsent(productName, k -> new LinkedList<>()).add(product);
            } else {
                returnedProducts.add(product);
            }
        }

        return returnedProducts;
    }

    @Override
    public String getStateName() {
        return State.MAINTENANCE.name();
    }

}
