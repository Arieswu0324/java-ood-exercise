package tictactoegame.entity;

import tictactoegame.enums.ResultType;

public record GameResult(ResultType result, Player winner, String message) {

}
