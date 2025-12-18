package tictactoegame.chain;


public class VerticalCheck extends WinCheckHandler {
    @Override
    boolean checkMyRule(String[][] grid) {
        for (int i = 0; i < 3; i++) {
            if (grid[0][i].equals(grid[1][i]) && grid[1][i].equals(grid[2][i])) {
                if (isValidSymbol(grid[0][i])) {
                    return true;
                }
            }

        }
        return false;
    }

}
