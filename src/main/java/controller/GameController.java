package controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import model.board.*;
import model.exception.*;

public interface GameController {
    GameState getCurrentState();

    void startGame();

    void beginTurn();

    void endTurn();

    Set<Move> getMovesForPieceAt(Position p);

    void makeMove(Move possibleMove) throws IllegalMoveException;

    Map<Piece, Position> getAllActivePiecesPositions();

    void addMoveListener(BiConsumer<Move, List<Position>> moveListener);

    void addGameStateChangeListener(Consumer<GameState> listener);

    void addCurrentSideListener(Consumer<Side> sideListener);

    Side getCurrentSide();

    String getSymbolForPieceAt(Position pos);

    boolean moveResultsInCapture(Move m);

    GameController getNewInstance();
}
