package vendingmachine.entity;

import vendingmachine.enums.Money;
import vendingmachine.state.VendingMachineState;

import java.util.Map;

public record TransactionResult(Map<Money, Integer> result, VendingMachineState nextState) {
}
