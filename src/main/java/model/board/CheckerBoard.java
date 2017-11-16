package model.board;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import model.exception.*;

public class CheckerBoard implements Board {
    private Map<Piece, Position> whitePositions;
    private Map<Piece, Position> blackPositions;
    private Piece[][] board;

    public CheckerBoard() {
        whitePositions = new HashMap<>();
        blackPositions = new HashMap<>();
        board = new CheckerPiece[8][8];
        for (Side side : Side.values()) {
            int startRow = -1;
            if (side.equals(Side.BLACK)) {
                startRow = 0;
            } else {
                startRow = 5;
            }
            for (int i = 0; i < 3; i++) {
                int startCol = -1;
                if ((startRow + i) % 2 == 0) {
                    startCol = 1;
                } else {
                    startCol = 0;
                }
                for (int j = 0; startCol + j < 8; j += 2) {
                    placePiece(new Men(side), new Position(startRow + i, startCol + j));
                }
            }
        }
    }

    private void placePiece(Piece p, Position pos) {
        if (p.getSide().equals(Side.BLACK)) {
            blackPositions.put(p, pos);
        } else {
            whitePositions.put(p, pos);
        }
        board[pos.getRow()][pos.getCol()] = p;
    }

    public void movePiece(Piece p, Move m) {
        Position destPos = m.getDestination();
        removePiece(p);
        placePiece(p, destPos);
        if (m instanceof CaptureMove) {
            CaptureMove capM = (CaptureMove) m;
            for (Position position : capM.getCapturePositions()) {
                Piece capturedPiece = getPieceAt(position);
                removePiece(capturedPiece);
            }
        }
        if (destPos.getRow() == (p.getSide() == Side.WHITE ? 0 : 7) && (p instanceof Men)) {
            replacePieceAt(destPos, new King(p.getSide()));
        }
    }

    public Map<Piece, Set<Move>> generateAllMovesForSide(Side s) throws SideHasNoMovesException {
        Map<Piece, Set<Move>> allMoves = new HashMap<Piece, Set<Move>>();
        Map<Piece, Position> piecePositions = (s == Side.WHITE) ? whitePositions : blackPositions;
        boolean sideHasMoves = false;
        boolean mustCapture = false;
        boolean cleared = false;
        for (Piece p : piecePositions.keySet()) {
            Set<Move> moves = p.generateMoves(piecePositions.get(p));
            Set<Move> captureMoves = new HashSet<Move>();
            Set<Move> filtered = moves.stream().filter(m -> pieceCanMove(m, s)).collect(Collectors.toCollection(HashSet<Move>::new));
            if (!filtered.isEmpty()) {
                sideHasMoves = true;
            }
            for (Move move : filtered) {
                if (getPieceAt(move.getDestination()) != null) {
                    Position start = move.getStart();
                    Position dest = move.getDestination();
                    Set<Position> visited = new HashSet<Position>();
                    visited.add(start);
                    int dy = dest.getRow() - start.getRow();
                    int dx = dest.getCol() - start.getCol();
                    generateCaptureMoves(captureMoves, start, start, new Position(dest.getRow() + dy, dest.getCol() + dx), p, visited, null);
                }
            }
            if (!captureMoves.isEmpty()) {
                mustCapture = true;
            }
            if (mustCapture && !cleared) {
                for (Piece mappedPiece : allMoves.keySet()) {
                    allMoves.put(mappedPiece, new HashSet<Move>());
                }
                cleared = true;
            }
            if (mustCapture) {
                allMoves.put(p, captureMoves);
            } else {
                allMoves.put(p, filtered);
            }
        }
        if (!sideHasMoves) {
            throw new SideHasNoMovesException(s.toString() + " has no moves.");
        }
        return allMoves;
    }

    private void generateCaptureMoves(Set<Move> captureMoves, Position start, Position midPoint, Position end, Piece mover, Set<Position> visited, Set<Position> captured) {
        if (CheckerUtils.posBoundsTest(end) && getPieceAt(end) == null && !visited.contains(end) && getPieceAt((midPoint.getRow() + end.getRow()) / 2, (midPoint.getCol() + end.getCol()) / 2) != null && getPieceAt((midPoint.getRow() + end.getRow()) / 2, (midPoint.getCol() + end.getCol()) / 2).getSide() != mover.getSide()) {
            CaptureMove newMove = new CaptureMove(start, end);
            if (captured != null) {
                newMove.addCapturePositions(captured);
            }
            newMove.addCapturePosition(new Position((midPoint.getRow() + end.getRow()) / 2, (midPoint.getCol() + end.getCol()) / 2));
            captureMoves.add(newMove);
            visited.add(end);
            if (mover instanceof King) {
                generateCaptureMoves(captureMoves, start, end, new Position(end.getRow() + 2, end.getCol() + 2), mover, visited, newMove.getCapturePositions());
                generateCaptureMoves(captureMoves, start, end, new Position(end.getRow() + 2, end.getCol() - 2), mover, visited, newMove.getCapturePositions());
                generateCaptureMoves(captureMoves, start, end, new Position(end.getRow() - 2, end.getCol() + 2), mover, visited, newMove.getCapturePositions());
                generateCaptureMoves(captureMoves, start, end, new Position(end.getRow() - 2, end.getCol() - 2), mover, visited, newMove.getCapturePositions());
            } else {
                if (mover.getSide().equals(Side.BLACK)) {
                    generateCaptureMoves(captureMoves, start, end, new Position(end.getRow() + 2, end.getCol() + 2), mover, visited, newMove.getCapturePositions());
                    generateCaptureMoves(captureMoves, start, end, new Position(end.getRow() + 2, end.getCol() - 2), mover, visited, newMove.getCapturePositions());
                } else {
                    generateCaptureMoves(captureMoves, start, end, new Position(end.getRow() - 2, end.getCol() + 2), mover, visited, newMove.getCapturePositions());
                    generateCaptureMoves(captureMoves, start, end, new Position(end.getRow() - 2, end.getCol() - 2), mover, visited, newMove.getCapturePositions());
                }
            }
        }
    }

    public boolean pieceCanMove(Move m, Side movingSide) {
        Position start = m.getStart();
        Position dest = m.getDestination();
        Piece mover = getPieceAt(start);
        Piece pieceAtDest = getPieceAt(dest);
        boolean positionAvailable = pieceAtDest == null;
        boolean canCapture = (!positionAvailable && !pieceAtDest.getSide().equals(movingSide));
        if (!positionAvailable && !canCapture) {
            return false;
        }
        if (!positionAvailable && canCapture) {
            int dy = dest.getRow() - start.getRow();
            int dx = dest.getCol() - start.getCol();
            return CheckerUtils.posBoundsTest(dest.getRow() + dy, dest.getCol() + dx) && getPieceAt(dest.getRow() + dy, dest.getCol() + dx) == null;
        }
        return true;
    }

    public Map<Piece, Position> getAllActivePiecesPositions() {
        Map<Piece, Position> all = new HashMap<Piece, Position>();
        all.putAll(blackPositions);
        all.putAll(whitePositions);
        return all;
    }

    public Piece getPieceAt(Position p) {
        return board[p.getRow()][p.getCol()];
    }

    private Piece getPieceAt(int row, int col) {
        return board[row][col];
    }

    public void replacePieceAt(Position pos, Piece newPiece) {
        Piece old = getPieceAt(pos);
        removePiece(old);
        placePiece(newPiece, pos);
    }

    private void removePiece(Piece p) {
        Position pos;
        if (p.getSide().equals(Side.BLACK)) {
            pos = blackPositions.get(p);
            blackPositions.remove(p);
        } else {
            pos = whitePositions.get(p);
            whitePositions.remove(p);
        }
        board[pos.getRow()][pos.getCol()] = null;
    }
}
