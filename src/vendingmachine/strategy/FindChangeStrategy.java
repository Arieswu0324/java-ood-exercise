package vendingmachine.strategy;

import vendingmachine.enums.Money;
import vendingmachine.exception.InsufficientChangeException;

import java.util.Map;
import java.util.TreeMap;

public interface FindChangeStrategy {
    Map<Money, Integer> findChange(int change, TreeMap<Money, Integer> snapshot) throws InsufficientChangeException;
}
