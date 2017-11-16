package model.board;

import java.util.Set;

public interface Piece {
    Set<Move> generateMoves(Position pos);

    Side getSide();

    PieceType getType();
}
