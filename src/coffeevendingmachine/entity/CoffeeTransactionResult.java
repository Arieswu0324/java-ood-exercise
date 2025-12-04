package coffeevendingmachine.entity;

import coffeevendingmachine.enums.Coin;


import java.util.Map;

public record CoffeeTransactionResult(Coffee coffee, Map<Coin, Integer> change) { }
