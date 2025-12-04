package coffeevendingmachine.entity;

import coffeevendingmachine.enums.CoffeeType;
import coffeevendingmachine.enums.Coin;
import coffeevendingmachine.enums.Ingredient;
import coffeevendingmachine.factory.CoffeeFactory;
import coffeevendingmachine.strategy.FindChangeStrategy;

import java.util.Map;

public record CoffeeMachineStateContext(Map<CoffeeType, Integer> menu,
                                        Map<Ingredient, Integer> inventory,
                                        Map<Ingredient, Integer> limit,
                                        FindChangeStrategy strategy,
                                        Map<Coin, Integer> money,
                                        CoffeeFactory factory) {
}
