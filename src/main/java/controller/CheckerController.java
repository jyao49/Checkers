package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import model.board.*;
import model.exception.*;

public class CheckerController implements GameController {
    private CheckerBoard board;
    private Side currentSide;
    private Map<Piece, Set<Move>> currentMoves;
    private List<BiConsumer<Move, List<Position>>> moveCallbacks;
    private List<Consumer<GameState>> stateCallbacks;
    private List<Consumer<Side>> sideCallbacks;
    private Piece selectedPiece;
    private GameState currentState;

    public CheckerController() {
        moveCallbacks = new ArrayList<>();
        stateCallbacks = new ArrayList<>();
        sideCallbacks = new ArrayList<>();
        board = new CheckerBoard();
        setCurrentState(CheckerState.ONGOING);
        setCurrentSide(Side.WHITE);
        currentMoves = new HashMap<>();
    }

    private void setCurrentState(GameState state) {
        currentState = state;
        for (Consumer<GameState> listener : stateCallbacks) {
            listener.accept(currentState);
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void startGame() {
        currentState = CheckerState.ONGOING;
        beginTurn();
    }

    public void beginTurn() {
        try {
            currentMoves = board.generateAllMovesForSide(currentSide);
            for (Piece piece : currentMoves.keySet()) {
                for (Move move : currentMoves.get(piece)) {
                    if (move instanceof CaptureMove) {
                        setCurrentState(CheckerState.MUST_CAPTURE);
                    }
                }
            }
        } catch (SideHasNoMovesException e) {
            currentMoves = null;
            try {
                board.generateAllMovesForSide((currentSide == Side.WHITE) ? Side.BLACK : Side.WHITE);
                setCurrentState((currentSide == Side.WHITE) ? CheckerState.BLACK_WINS : CheckerState.WHITE_WINS);
            } catch (SideHasNoMovesException f) {
                setCurrentState(CheckerState.STALEMATE);
            }
        }
    }

    public void endTurn() {
        if (currentSide == Side.WHITE) {
            setCurrentSide(Side.BLACK);
        } else {
            setCurrentSide(Side.WHITE);
        }
        setCurrentState(CheckerState.ONGOING);
    }

    public Set<Move> getMovesForPieceAt(Position p) {
        Piece piece = board.getPieceAt(p);
        return (piece == null || piece.getSide() != currentSide) ? new HashSet<>() : currentMoves.get(piece);
    }

    public void makeMove(Move possibleMove) throws IllegalMoveException {
        Piece mover = board.getPieceAt(possibleMove.getStart());
        selectedPiece = mover;
        Move m = queryForMove(possibleMove);
        if (m == null) {
            throw new IllegalMoveException(possibleMove);
        }
        if (currentMoves != null && currentMoves.get(mover) != null && currentMoves.get(mover).contains(m)) {
            board.movePiece(mover, m);
            List<Position> capturePositions = new ArrayList<>();
            if (m instanceof CaptureMove) {
                CaptureMove cm = (CaptureMove) m;
                capturePositions.addAll(cm.getCapturePositions());
            }
            for (BiConsumer<Move, List<Position>> callback : moveCallbacks) {
                callback.accept(m, capturePositions);
            }
        } else {
            throw new IllegalMoveException(possibleMove);
        }
    }

    private Move queryForMove(Move m) {
        Move found = null;
        for (Move test : currentMoves.get(selectedPiece)) {
            if (m.equals(test)) {
                found = test;
            }
        }
        return found;
    }

    public Map<Piece, Position> getAllActivePiecesPositions() {
        return board.getAllActivePiecesPositions();
    }

    public void addMoveListener(BiConsumer<Move, List<Position>> moveListener) {
        moveCallbacks.add(moveListener);
    }

    public void addGameStateChangeListener(Consumer<GameState> listener) {
        stateCallbacks.add(listener);
    }

    public void addCurrentSideListener(Consumer<Side> sideListener) {
        sideCallbacks.add(sideListener);
    }

    public Side getCurrentSide() {
        return currentSide;
    }

    private void setCurrentSide(Side currentSide) {
        this.currentSide = currentSide;
        for (Consumer<Side> sides : sideCallbacks) {
            sides.accept(currentSide);
        }
    }

    public String getSymbolForPieceAt(Position pos) {
        Piece p = board.getPieceAt(pos);
        if (p == null) {
            return "";
        }
        return p.getType().getSymbol(p.getSide());
    }

    public boolean moveResultsInCapture(Move m) {
        return (m instanceof CaptureMove);
    }

    public GameController getNewInstance() {
        return new CheckerController();
    }
}
