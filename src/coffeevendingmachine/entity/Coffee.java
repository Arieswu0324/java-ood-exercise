package coffeevendingmachine.entity;

import coffeevendingmachine.enums.CoffeeType;
import coffeevendingmachine.enums.Ingredient;

import java.util.Map;

public class Coffee {
    private final CoffeeType type;
    private final Map<Ingredient, Integer> recipe;


    public Coffee(CoffeeType type, Map<Ingredient, Integer> recipe) {
        this.type = type;
        this.recipe = recipe;
    }

    public Map<Ingredient, Integer> getRecipe() {
        return recipe;
    }
}
