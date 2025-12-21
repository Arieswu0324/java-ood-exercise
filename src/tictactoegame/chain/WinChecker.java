package tictactoegame.chain;

import tictactoegame.enums.Symbol;

public abstract class WinChecker {

    private WinChecker nextCheck;

    //判断赢局的方法，传入的是棋盘，返回的是布尔类型，赢局为true
    public boolean check(String[][] grid) {
        if (checkMyRule(grid)) {
            return true;
        }
        if (nextCheck != null) {
            return nextCheck.check(grid);
        }
        return false;
    }

    public void setNextCheck(WinChecker nextCheck) {
        this.nextCheck = nextCheck;
    }

    //子类各自实现的处理逻辑
    protected abstract boolean checkMyRule(String[][] grid);

    //子类的公共 helper 方法
    protected boolean isValidSymbol(String s) {
        return s.equals(Symbol.X.name()) || s.equals(Symbol.O.name());
    }
}
