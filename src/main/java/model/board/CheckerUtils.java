package model.board;

public class CheckerUtils {
    public static boolean posBoundsTest(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public static boolean posBoundsTest(Position p) {
        return posBoundsTest(p.getRow(), p.getCol());
    }

    public static Piece getPieceOfType(PieceType pieceType, Side s) {
        if (pieceType.equals(CheckerPiece.CheckerPieceType.MEN)) {
            return new Men(s);
        } else if (pieceType.equals(CheckerPiece.CheckerPieceType.KING)) {
            return new King(s);
        }
        return null;
    }

    public static PieceType getPieceTypeFromString(String s) {
        if (s.equals("MEN")) {
            return CheckerPiece.CheckerPieceType.MEN;
        } else if (s.equals("KING")) {
            return CheckerPiece.CheckerPieceType.KING;
        }
        return null;
    }
}
