package coffeevendingmachine.factory;

import coffeevendingmachine.entity.Coffee;
import coffeevendingmachine.enums.CoffeeType;
import coffeevendingmachine.enums.Ingredient;
import coffeevendingmachine.exceptions.UnsupportedCoffeeTypeException;

import java.util.Map;

public class CoffeeFactory {

    public static CoffeeFactory INSTANCE = new CoffeeFactory();

    private CoffeeFactory() {
    }

    public static CoffeeFactory getInstance() {
        return INSTANCE;
    }


    public static final Map<CoffeeType, Map<Ingredient, Integer>> recipes =
            Map.of(CoffeeType.ESPRESSO, Map.of(Ingredient.BEAN, 50, Ingredient.MILK, 25, Ingredient.SUGAR, 5),
                    CoffeeType.CAPPUCCINO, Map.of(Ingredient.BEAN, 30, Ingredient.CREAM, 30),
                    CoffeeType.LATTE, Map.of(Ingredient.BEAN, 25, Ingredient.CREAM, 10, Ingredient.MILK, 10));

    public Coffee make(CoffeeType type) throws UnsupportedCoffeeTypeException, InterruptedException {
        Coffee coffee;

        switch (type) {
            case ESPRESSO -> coffee = new Coffee(CoffeeType.ESPRESSO, recipes.get(CoffeeType.ESPRESSO));
            case CAPPUCCINO -> coffee = new Coffee(CoffeeType.CAPPUCCINO, recipes.get(CoffeeType.CAPPUCCINO));
            case LATTE -> coffee = new Coffee(CoffeeType.LATTE, recipes.get(CoffeeType.LATTE));
            default -> throw new UnsupportedCoffeeTypeException(type);
        }
        shake();
        return coffee;
    }

    private void shake() throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("making coffee...");
    }

}
