package model.board;

public class NormalMove implements Move {
    private Position start;
    private Position destination;

    public NormalMove(Position start, Position destination) {
        this.start = start;
        this.destination = destination;
    }

    public Position getStart() {
        return start;
    }

    public Position getDestination() {
        return destination;
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
