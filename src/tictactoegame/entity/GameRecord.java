package tictactoegame.entity;

import tictactoegame.enums.Symbol;

public record GameRecord(Symbol nextTurn, GameResult result) {
}
