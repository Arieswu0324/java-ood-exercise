package tictactoegame.entity;

import tictactoegame.enums.Symbol;

public class Player {
    private final String alias;
    private final Symbol symbol;

    public Player(Symbol symbol, String alias) {
        this.alias = alias;
        this.symbol = symbol;
    }


    public String getAlias() {
        return this.alias;
    }

    public Symbol getSymbol() {
        return symbol;
    }
}
