package model.board;

import java.util.HashSet;
import java.util.Set;

public class King extends CheckerPiece {
    public King(Side side) {
        super(CheckerPieceType.KING, side);
    }

    public Set<Move> generateMoves(Position curPos) {
        Set<Move> moves = new HashSet<Move>();
        int curRow = curPos.getRow();
        int curCol = curPos.getCol();
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dy = -1; dy <= 1; dy += 2) {
                int destRow = curRow + dy;
                int destCol = curCol + dx;
                if (CheckerUtils.posBoundsTest(destRow, destCol)) {
                    Position destPos = new Position(destRow, destCol);
                    moves.add(new NormalMove(curPos, destPos));
                }
            }
        }
        return moves;
    }
}
