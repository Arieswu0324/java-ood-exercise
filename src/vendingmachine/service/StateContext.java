package vendingmachine.service;

import vendingmachine.entity.Product;
import vendingmachine.enums.Money;
import vendingmachine.strategy.FindChangeStrategy;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public record StateContext(
        Map<String, Integer> products,
        Map<String, List<Product>> stocking,
        TreeMap<Money, Integer> funds,
        FindChangeStrategy strategy,
        Map<String, Integer> quantities
){}