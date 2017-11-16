package model.board;

import java.util.Set;

public abstract class CheckerPiece implements Piece {
    private static int refCount;
    private int id;
    private Side side;
    private CheckerPieceType type;

    public CheckerPiece(CheckerPieceType type, Side side) {
        id = refCount++;
        this.type = type;
        this.side = side;
    }

    public abstract Set<Move> generateMoves(Position curPos);

    public int hashCode() {
        return id;
    }

    public Side getSide() {
        return side;
    }

    public PieceType getType() {
        return type;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nChessPiece Type: ");
        sb.append(type.toString());
        sb.append("\n Side: ");
        sb.append(side.toString());
        sb.append("\n ID: ");
        sb.append(id);
        return sb.toString();
    }

    public enum CheckerPieceType implements PieceType {
        MEN("\u26C0", "\u26C2"), KING("\u26C1", "\u26C3");

        private String white;
        private String black;

        public String getSymbol(Side pieceSide) {
            return pieceSide == Side.WHITE ? white : black;
        }

        CheckerPieceType(String white, String black) {
            this.white = white;
            this.black = black;
        }
    }

}
