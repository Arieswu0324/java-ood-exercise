package coffeevendingmachine.observer;

import coffeevendingmachine.enums.Ingredient;

import java.util.List;

public interface RefillObserver {

    void onEvent(List<Ingredient> ingredient);
}
