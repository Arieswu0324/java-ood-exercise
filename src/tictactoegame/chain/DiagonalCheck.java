package tictactoegame.chain;

public class DiagonalCheck extends WinCheckHandler {
    @Override
    boolean checkMyRule(String[][] grid) {

        if (grid[0][0].equals(grid[1][1]) && grid[1][1].equals(grid[2][2])) {
            if (isValidSymbol(grid[0][0])) {
                return true;
            }
        }

        if (grid[2][0].equals(grid[1][1]) && grid[1][1].equals(grid[0][2])) {
            if (isValidSymbol(grid[2][0])) {
                return true;
            }
        }

        return false;
    }
}
