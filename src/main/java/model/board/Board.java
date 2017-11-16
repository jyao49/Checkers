package model.board;

import java.util.Map;
import java.util.Set;

import model.exception.*;

public interface Board {
    void movePiece(Piece p, Move m);

    Map<Piece, Set<Move>> generateAllMovesForSide(Side s) throws SideHasNoMovesException;

    boolean pieceCanMove(Move m, Side movingSide);

    Map<Piece, Position> getAllActivePiecesPositions();

    Piece getPieceAt(Position p);

    void replacePieceAt(Position pos, Piece newPiece);
}
