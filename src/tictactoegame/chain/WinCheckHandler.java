package tictactoegame.chain;

import tictactoegame.enums.Symbol;

public abstract class WinCheckHandler {
    protected WinCheckHandler nextCheck;

    public boolean check(String[][] grid){
        if(checkMyRule(grid)){
            return true;
        }
        if(nextCheck!=null){
            return nextCheck.check(grid);
        }
        return false;
    }

    public void setNextCheck(WinCheckHandler nextCheck) {
        this.nextCheck = nextCheck;
    }

    abstract boolean checkMyRule(String[][] grid);

    protected boolean isValidSymbol(String s){
        return s.equals(Symbol.X.name()) || s.equals(Symbol.O.name());
    }
}
