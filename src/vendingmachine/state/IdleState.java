package vendingmachine.state;

import vendingmachine.service.StateContext;
import vendingmachine.constants.VendingMachineOperationConstants;
import vendingmachine.entity.Product;
import vendingmachine.entity.TransactionResult;
import vendingmachine.enums.Money;
import vendingmachine.enums.State;
import vendingmachine.exception.*;
import vendingmachine.strategy.FindChangeStrategy;

import java.util.*;

public class IdleState implements VendingMachineState {


    @Override
    public TransactionResult dispense(StateContext context, String product, Map<Money, Integer> intake) throws VendingMachineException {

        Map<String, Integer> products = context.products();
        Map<String, List<Product>> stocking = context.stocking();
        TreeMap<Money, Integer> funds = context.funds();
        FindChangeStrategy strategy = context.strategy();


        if (!products.containsKey(product)) {
            throw new InvalidProductException(product);
        }

        if (!stocking.containsKey(product) || stocking.get(product).isEmpty()) {
            throw new OutOfStockException(product);
        }

        // Calculate change
        int inputMoney = calculateTotal(intake);
        int productPrice = products.get(product);
        int changeAmount = inputMoney - productPrice;

        // Insufficient intake
        if (changeAmount < 0) {
            throw new InsufficientFundsException(productPrice, inputMoney);
        }

        // Create snapshot of available funds
        TreeMap<Money, Integer> fundSnapshot = createFundSnapshot(funds);
        // 这里又创建了一个副本，避免策略会修改快照
        TreeMap<Money, Integer> snapshotCopy = createFundSnapshot(funds);
        Map<Money, Integer> changePlan = strategy.findChange(changeAmount, snapshotCopy);

        //Action
        commitTransaction(funds, stocking, product, fundSnapshot, intake, changePlan);

        VendingMachineState nextState = stocking.isEmpty() ? new OutOfServiceState() : new IdleState();

        return new TransactionResult(changePlan, nextState);
    }

    @Override
    public Map<Money, Integer> collectMoney(StateContext context) throws InvalidStateException {
        throw new InvalidStateException(getStateName(), VendingMachineOperationConstants.COLLECT_MONEY);
    }

    @Override
    public List<Product> refill(StateContext context, List<Product> products) throws InvalidStateException {
        throw new InvalidStateException(getStateName(), VendingMachineOperationConstants.REFILL);
    }

    @Override
    public String getStateName() {
        return State.IDLE.name();
    }

    private int calculateTotal(Map<Money, Integer> intake) {
        return intake.entrySet().stream()
                .mapToInt(entry -> entry.getKey().getDenomination() * entry.getValue()).sum();
    }

    private void commitTransaction(Map<Money, Integer> funds, Map<String, List<Product>> stocking, String product, TreeMap<Money, Integer> fundSnapshot, Map<Money, Integer> intake, Map<Money, Integer> changePlan) throws VendingMachineException {

        TransactionSnapshot snapshot = new TransactionSnapshot(product,
                stocking.get(product).getLast(),
                fundSnapshot,
                intake,
                changePlan);

        try {
            // Commit transaction atomically
            // 1. Remove product from stock
            stocking.get(product).removeLast();
            if (stocking.get(product).isEmpty()) {
                stocking.remove(product);
            }

            // 2. Add customer payment to funds
            saveMoney(funds, intake);

            // 3. Remove change from funds
            returnMoney(funds, changePlan);

        } catch (Exception e) {
            rollback(funds, stocking, snapshot);
            throw new VendingMachineException("Transaction failed and rolled back", e);
        }
    }

    //Memento Pattern
    private record TransactionSnapshot(String product, Product outProduct,
                                       TreeMap<Money, Integer> fundsBefore,
                                       Map<Money, Integer> intake,
                                       Map<Money, Integer> changePlan) {
    }

    private void rollback(Map<Money, Integer> funds, Map<String, List<Product>> stocking, TransactionSnapshot snapshot) {
        String productName = snapshot.product;
        stocking.computeIfAbsent(productName, k -> new LinkedList<>()).add(snapshot.outProduct());
        funds.clear();
        funds.putAll(snapshot.fundsBefore());
    }

    private void saveMoney(Map<Money, Integer> funds, Map<Money, Integer> money) {
        for (Map.Entry<Money, Integer> entry : money.entrySet()) {
            funds.compute(entry.getKey(), (k, v) -> {
                if (v == null) {
                    v = entry.getValue();
                } else {
                    v += entry.getValue();
                }
                return v;
            });
        }
    }

    private void returnMoney(Map<Money, Integer> funds, Map<Money, Integer> money) {
        if (money.isEmpty()) {
            return;
        }
        for (Map.Entry<Money, Integer> entry : money.entrySet()) {
            funds.computeIfPresent(entry.getKey(), (k, v) -> {
                v = v - entry.getValue();
                return v <= 0 ? null : v;
            });
        }

    }


}
