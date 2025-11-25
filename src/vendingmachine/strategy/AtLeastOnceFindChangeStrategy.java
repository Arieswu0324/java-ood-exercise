package vendingmachine.strategy;

import vendingmachine.enums.Money;
import vendingmachine.exception.InsufficientChangeException;

import java.util.*;

public class AtLeastOnceFindChangeStrategy implements FindChangeStrategy{
    /**
     * Uses dynamic programming to find optimal change combination.
     * This ensures we find a solution whenever one exists, unlike greedy approach.
     *
     * Algorithm: Coin change problem with limited quantities.
     * Time complexity: O(amount * total_coins)
     * Space complexity: O(amount)
     *
     * @param change Amount of change needed in pence
     * @return Map of Money denominations to counts for making change
     * @throws InsufficientChangeException if exact change cannot be made with available denominations
     */
    @Override
    public Map<Money, Integer> findChange(int change, TreeMap<Money, Integer> snapshot) throws InsufficientChangeException {
        if (change == 0) {
            return new HashMap<>();
        }

        // Build list of available coins/notes with their counts
        List<Money> availableDenominations = new ArrayList<>();
        for (Map.Entry<Money, Integer> entry : snapshot.entrySet()) {
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
        for (Money money : availableDenominations) {
            int denomination = money.getDenomination();

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
            throw new InsufficientChangeException(change);
        }

        // Reconstruct the solution by backtracking through parent map
        Map<Money, Integer> output = new HashMap<>();
        int current = change;

        while (current > 0) {
            int[] step = parent.get(current);
            int usedDenomination = step[1];

            // Find the Money enum for this denomination from available funds
            Money usedMoney = null;
            for (Money money : availableDenominations) {
                if (money.getDenomination() == usedDenomination) {
                    usedMoney = money;
                    availableDenominations.remove(money); // Remove to handle limited quantities
                    break;
                }
            }

            output.put(usedMoney, output.getOrDefault(usedMoney, 0) + 1);
            current = step[0];
        }

        return output;
    }
}
