package coffeevendingmachine.strategy;

import coffeevendingmachine.enums.Coin;
import coffeevendingmachine.exceptions.CannotMakeChangeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmallFirstStrategy implements FindChangeStrategy{
    @Override
    public Map<Coin, Integer> findChange(int change, Map<Coin, Integer> money) throws CannotMakeChangeException {
        if (change == 0) {
            return new HashMap<>();
        }

        // Build list of available coins/notes with their counts
        List<Coin> availableDenominations = new ArrayList<>();
        for (Map.Entry<Coin, Integer> entry : money.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                availableDenominations.add(entry.getKey());
            }
        }

        // DP array: dp[i] = true if we can make amount i
        boolean[] dp = new boolean[change + 1];
        dp[0] = true;

        // parent[i] stores {amount before, denomination used} to reach amount i
        Map<Integer, int[]> parent = new HashMap<>();

        // Try each denomination
        for (Coin c : availableDenominations) {
            int denomination = c.getD();

            // Traverse backwards to avoid using same coin multiple times in one iteration
            for (int amount = change; amount >= denomination; amount--) {
                if (dp[amount - denomination] && !dp[amount]) {
                    dp[amount] = true;
                    parent.put(amount, new int[]{amount - denomination, denomination});
                }
            }
        }

        // Check if we can make exact change
        if (!dp[change]) {
            throw new CannotMakeChangeException();
        }

        // Reconstruct the solution by backtracking through parent map
        Map<Coin, Integer> output = new HashMap<>();
        int current = change;

        while (current > 0) {
            int[] step = parent.get(current);
            int usedDenomination = step[1];

            // Find the Money enum for this denomination from available funds
            Coin usedMoney = null;
            for (Coin c : availableDenominations) {
                if (c.getD() == usedDenomination) {
                    usedMoney = c;
                    availableDenominations.remove(c); // Remove to handle limited quantities
                    break;
                }
            }

            output.put(usedMoney, output.getOrDefault(usedMoney, 0) + 1);
            current = step[0];
        }

        return output;
    }
}
