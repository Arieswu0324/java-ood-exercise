package coffeevendingmachine.observer;

import coffeevendingmachine.enums.Ingredient;

import java.util.List;

public class RingObserver implements RefillObserver {
    @Override
    public void onEvent(List<Ingredient> ingredient) {
        System.out.println("Ringing out to notify" + ingredient + " refill");
    }
}
