package tictactoegame.service;

import tictactoegame.chain.DiagonalChecker;
import tictactoegame.chain.HorizontalChecker;
import tictactoegame.chain.VerticalChecker;
import tictactoegame.chain.WinChecker;
import tictactoegame.entity.GameRecord;
import tictactoegame.entity.GameResult;
import tictactoegame.entity.Player;
import tictactoegame.enums.ResultType;
import tictactoegame.enums.Symbol;
import tictactoegame.exception.TicTacToeException;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class TicTacToeGame {
    private final String[][] grid;
    private final WinChecker winChecker;
    private final Map<String, Player> players;
    private final String xPlayer;
    private final String oPlayer;

    private Symbol next = Symbol.O;

    public TicTacToeGame(Scanner sc) {
        grid = new String[3][3];


        WinChecker verticalCheck = new VerticalChecker();
        WinChecker diagonalCheck = new DiagonalChecker();
        winChecker = new HorizontalChecker();
        winChecker.setNextCheck(verticalCheck);
        verticalCheck.setNextCheck(diagonalCheck);


        players = new HashMap<>();
        System.out.println("Please enter O player's alias");
        String oAlias = sc.nextLine();
        oPlayer = oAlias;
        players.put(oAlias, new Player(Symbol.O, oAlias));
        System.out.println("Please enter X player's alias");
        String xAlias = sc.nextLine();
        xPlayer = xAlias;
        players.put(xAlias, new Player(Symbol.X, xAlias));
        System.out.println("Ready to start");

    }


    public void start(Symbol symbol) {
        if (xPlayer.isBlank() || oPlayer.isBlank()) {
            throw new TicTacToeException("players not ready");
        }
        for (int i = 0; i < 3; i++) {
            Arrays.fill(grid[i], " ");
        }
        next = symbol;
        printBoard();
        System.out.println("game start: X player-" + xPlayer + " vs O player-" + oPlayer);
        System.out.println("first turn: " + symbol.name());
    }

    public Player getPlayer(String alias) {
        if (!players.containsKey(alias)) {
            throw new TicTacToeException("invalid player");
        }
        return players.get(alias);
    }


    public GameRecord move(int x, int y, Player player) {

        if (player == null || !next.equals(player.getSymbol())) {
            throw new TicTacToeException("invalid player");
        }

        if (x < 0 || x >= 3 || y < 0 || y >= 3) {
            throw new TicTacToeException("invalid position");
        }

        if (!grid[x][y].isBlank()) {
            throw new TicTacToeException("cannot move, position occupied");
        }

        grid[x][y] = next.name();
        next = getNextTurn(player.getSymbol());
        GameResult result = null;

        //check win
        if (checkWin()) {
            result = new GameResult(ResultType.WIN, player, "Congratulations, you win!");
            next = null;
        }

        //check draw
        if (checkDraw()) {
            result = new GameResult(ResultType.DRAW, null, "The game ends in a draw");
            next = null;
        }

        //print board
        printBoard();

        return new GameRecord(next, result);
    }

    private void printBoard() {
        System.out.println("_ _ _");
        for (int i = 0; i < 3; i++) {
            System.out.println(String.join("|", grid[i]));
            System.out.println("_ _ _");
        }
    }

    private Symbol getNextTurn(Symbol symbol) {
        if (Symbol.X.equals(symbol)) {
            return Symbol.O;
        }
        if (Symbol.O.equals(symbol)) {
            return Symbol.X;
        }

        throw new TicTacToeException("invalid turn state");
    }

    private boolean checkWin() {
        return winChecker.check(grid);
    }


    private String[][] copy(String[][] grid) {
        String[][] gridCopy = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gridCopy[i][j] = grid[i][j];
            }
        }
        return gridCopy;
    }

    private boolean checkDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i][j].isBlank()) {
                    return false;
                }
            }
        }

        return true;
    }

}
