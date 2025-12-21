package tictactoegame.chain;

public class DiagonalChecker extends WinChecker {
    @Override
    protected boolean checkMyRule(String[][] grid) {

        //45度对角
        if (grid[0][0].equals(grid[1][1]) && grid[1][1].equals(grid[2][2])) {
            if (isValidSymbol(grid[0][0])) {
                return true;
            }
        }

        //135度对角
        if (grid[2][0].equals(grid[1][1]) && grid[1][1].equals(grid[0][2])) {
            if (isValidSymbol(grid[2][0])) {
                return true;
            }
        }

        return false;
    }
}
