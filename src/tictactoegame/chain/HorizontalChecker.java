package tictactoegame.chain;


public class HorizontalChecker extends WinChecker {

    @Override
    protected boolean checkMyRule(String[][] grid) {
        for (int i = 0; i < 3; i++) {
            String[] row = grid[i];
            if (row[0].equals(row[1]) && row[1].equals(row[2])) {
                if (isValidSymbol(row[0])) {
                    return true;
                }
            }
        }
        return false;
    }
}
