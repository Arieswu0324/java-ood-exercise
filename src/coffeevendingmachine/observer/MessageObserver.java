package coffeevendingmachine.observer;

import coffeevendingmachine.enums.Ingredient;

import java.util.List;

public class MessageObserver implements RefillObserver {
    @Override
    public void onEvent(List<Ingredient> ingredient) {
        System.out.println("sending out message to notify " + ingredient + "refill");
    }
}
