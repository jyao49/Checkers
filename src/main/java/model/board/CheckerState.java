package model.board;

public enum CheckerState implements GameState {
    WHITE_WINS("White Wins"),
    BLACK_WINS("Black Wins"),
    STALEMATE("Stalemate"),
    MUST_CAPTURE("A Capture Move Must Be Made"),
    ONGOING("Ongoing");

    private String message;

    CheckerState(String message) {
        this.message = message;
    }

    public boolean isGameOver() {
        return equals(WHITE_WINS) || equals(BLACK_WINS) || equals(STALEMATE);
    }

    public String toString() {
        return message;
    }
}
