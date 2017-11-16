package model.exception;

import model.board.Move;

public class IllegalMoveException extends Exception {
    public IllegalMoveException(Move m) {
        super("Illegal Move: " + m.toString());
    }
}
