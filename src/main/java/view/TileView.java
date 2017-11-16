package view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import model.board.Position;

public class TileView  implements Tile {
    private Position p;
    private Label symbol;
    private Rectangle background;
    private Rectangle highlight;
    private StackPane tile;

    public TileView(Position p) {
        this.p = p;
        symbol = new Label("");
        background = new Rectangle(90, 80);
        highlight = new Rectangle(90, 80);
        tile = new StackPane();
        highlight.setFill(Color.TRANSPARENT);
        if ((p.getRow() + p.getCol()) % 2 == 0) {
            background.setFill(Color.WHITE);
        } else {
            background.setFill(Color.LIGHTGREY);
        }
        tile.getChildren().addAll(background, highlight, symbol);
        tile.setAlignment(symbol, Pos.TOP_CENTER);
        tile.setAlignment(highlight, Pos.CENTER);
        tile.setAlignment(background, Pos.BOTTOM_CENTER);
    }

    public Position getPosition() {
        return p;
    }

    public Node getRootNode() {
        return tile;
    }

    public void setSymbol(String symbol) {
        this.symbol.setText(symbol);
        this.symbol.setFont(new Font(53));
    }

    public String getSymbol() {
        return symbol.getText();
    }

    public void highlight(Color color) {
        highlight.setFill(color);
    }

    public void clear() {
        highlight.setFill(Color.TRANSPARENT);
    }
}
