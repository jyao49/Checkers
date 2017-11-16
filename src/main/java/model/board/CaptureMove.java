package model.board;

import java.util.Set;
import java.util.HashSet;

public class CaptureMove implements Move {
    private Position start;
    private Position destination;
    private Set<Position> capturePositions;

    public CaptureMove(Position start, Position destination) {
        this.start = start;
        this.destination = destination;
        capturePositions = new HashSet<Position>();
    }

    public Position getStart() {
        return start;
    }

    public Position getDestination() {
        return destination;
    }

    public void addCapturePosition(Position p) {
        capturePositions.add(p);
    }

    public void addCapturePositions(Set<Position> pSet) {
        capturePositions.addAll(pSet);
    }

    public Set<Position> getCapturePositions() {
        return capturePositions;
    }

    public int hashCode() {
        return start.hashCode() - destination.hashCode();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof Move)) {
            return false;
        }
        Move m = (Move) o;
        return start.equals(m.getStart()) && destination.equals(m.getDestination());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(start);
        sb.append("->");
        sb.append(destination);
        return sb.toString();
    }
}
