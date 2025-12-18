package tictactoegame;

import tictactoegame.entity.GameRecord;
import tictactoegame.entity.Player;
import tictactoegame.enums.Symbol;
import tictactoegame.service.TicTacToeGame;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TicTacToeGame game = new TicTacToeGame(sc);

        game.start(Symbol.X);

        GameRecord record;
        Symbol next = null;
        do {
            if (next != null) {
                System.out.println("next turn:  " + next.name());
            }
            int x = sc.nextInt();
            int y = sc.nextInt();
            System.out.println("enter player's alias: ");
            sc.nextLine();
            String alias = sc.nextLine();
            Player player = game.getPlayer(alias);

            record = game.move(x, y, player);
            next = record.nextTurn();
        } while (next != null);

        if (record.result() != null) {
            System.out.println(record.result().message());
            if (record.result().winner() != null) {
                System.out.println("Winner: " + record.result().winner().getAlias());
            }
        }

        sc.close();
    }
}
