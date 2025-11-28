package vendingmachine.state;

import vendingmachine.service.StateContext;
import vendingmachine.constants.VendingMachineOperationConstants;
import vendingmachine.entity.Product;
import vendingmachine.entity.TransactionResult;
import vendingmachine.enums.Money;
import vendingmachine.enums.State;
import vendingmachine.exception.InvalidStateException;
import vendingmachine.exception.VendingMachineException;

import java.util.List;
import java.util.Map;

public class OutOfServiceState implements VendingMachineState {
    @Override
    public TransactionResult dispense(StateContext context, String product, Map<Money, Integer> intake) throws VendingMachineException {
        throw new InvalidStateException(getStateName(), VendingMachineOperationConstants.DISPENSE);
    }

    @Override
    public Map<Money, Integer> collectMoney(StateContext context) throws VendingMachineException {
        throw new InvalidStateException(getStateName(), VendingMachineOperationConstants.COLLECT_MONEY);
    }

    @Override
    public List<Product> refill(StateContext context, List<Product> products) throws VendingMachineException {
        throw new InvalidStateException(getStateName(), VendingMachineOperationConstants.REFILL);
    }

    @Override
    public Map<String, List<Product>> getAvailableProducts(Map<String, List<Product>> stocking) throws VendingMachineException {
        throw new InvalidStateException(getStateName(), VendingMachineOperationConstants.CHECK_AVAILABILITY);
    }

    @Override
    public String getStateName() {
        return State.OUT_OF_SERVICE.name();
    }
}
