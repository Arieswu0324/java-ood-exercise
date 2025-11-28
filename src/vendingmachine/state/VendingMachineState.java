package vendingmachine.state;

import vendingmachine.service.StateContext;
import vendingmachine.entity.Product;
import vendingmachine.entity.TransactionResult;
import vendingmachine.enums.Money;
import vendingmachine.exception.VendingMachineException;

import java.util.*;

public interface VendingMachineState {

    TransactionResult dispense(StateContext context, String product, Map<Money, Integer> intake) throws VendingMachineException;

    Map<Money, Integer> collectMoney(StateContext context) throws VendingMachineException;

    List<Product> refill(StateContext context, List<Product> products) throws VendingMachineException;

    default Map<String, List<Product>> getAvailableProducts(Map<String, List<Product>> stocking) throws VendingMachineException {
        Map<String, List<Product>> inventory = new HashMap<>();
        for (Map.Entry<String, List<Product>> entry : stocking.entrySet()) {
            String key = entry.getKey();

            List<Product> products = new LinkedList<>(entry.getValue());
            inventory.put(key, products);//product是不可变类，所有变量final，没有setter，所以可以直接用，否则要再拷贝product这一层
        }

        return inventory;
    }

    String getStateName();

    default TreeMap<Money, Integer> createFundSnapshot(TreeMap<Money, Integer> funds) {
        TreeMap<Money, Integer> fundSnapshot;
        if (funds.comparator() != null) {
            fundSnapshot = new TreeMap<>(funds.comparator());
        } else {
            fundSnapshot = new TreeMap<>();
        }
        fundSnapshot.putAll(funds);

        return fundSnapshot;
    }



}
