package model.board;

import java.util.HashSet;
import java.util.Set;

public class Men extends CheckerPiece {
    public Men(Side side) {
        super(CheckerPieceType.MEN, side);
    }

    public Set<Move> generateMoves(Position curPos) {
        Set<Move> moves = new HashSet<Move>();
        int curRow = curPos.getRow();
        int curCol = curPos.getCol();
        int dy = 1;
        if (getSide().equals(Side.WHITE)) {
            dy = -1;
        }
        for (int dx = -1; dx <= 1; dx += 2) {
            int destRow = curRow + dy;
            int destCol = curCol + dx;
            if (CheckerUtils.posBoundsTest(destRow, destCol)) {
                Position destPos = new Position(destRow, destCol);
                moves.add(new NormalMove(curPos, destPos));
            }
        }
        return moves;
    }
}
