package view;

import javafx.scene.Node;
import javafx.scene.paint.Color;

import model.board.Position;

public interface Tile {
    Position getPosition();

    Node getRootNode();

    void setSymbol(String symbol);

    String getSymbol();

    void highlight(Color color);

    void clear();
}
