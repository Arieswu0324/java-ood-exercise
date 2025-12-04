package coffeevendingmachine.strategy;

import coffeevendingmachine.enums.Coin;
import coffeevendingmachine.exceptions.CannotMakeChangeException;

import java.util.Map;

public interface FindChangeStrategy {

    Map<Coin, Integer> findChange(int output, Map<Coin, Integer> money) throws CannotMakeChangeException;

}
