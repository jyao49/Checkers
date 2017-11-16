package model.board;

public enum Side {

    WHITE(5, 7, "White"),
    BLACK(2, 0, "Black");

    private int frontRow;
    private int backRow;
    private String sideName;

    Side(int frontRow, int backRow, String sideName) {
        this.frontRow = frontRow;
        this.backRow = backRow;
        this.sideName = sideName;
    }

    public int getFrontRow() {
        return frontRow;
    }

    public int getBackRow() {
        return backRow;
    }

    @Override
    public String toString() {
        return sideName;
    }
}
