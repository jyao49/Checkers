package view;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import controller.*;
import model.board.*;
import model.exception.*;

public class BoardView {
    private GameController controller;
    private GridPane gridPane;
    private Tile[][] tiles;
    private Text sideStatus;
    private Text stateStatus;
    private Position curPos;
    private Position destPos;
    private Move lastMove = new NormalMove(new Position(0, 0), new Position(0,0));
    private boolean hasClicked;
    private Set<Move> moves;

    public BoardView(GameController controller, Text stateStatus, Text sideStatus) {
        this.controller = controller;
        this.stateStatus = stateStatus;
        this.sideStatus = sideStatus;
        tiles = new Tile[8][8];
        gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color : goldenrod;");
        reset(controller);
    }

    private EventHandler<? super MouseEvent> tileListener(Tile tile) {
        return event -> {
            if (hasClicked) {
                secondClick(tile);
            } else {
                firstClick(tile);
            }
        };
    }

    private void firstClick(Tile tile) {
        curPos = tile.getPosition();
        moves = controller.getMovesForPieceAt(curPos);
        if (!moves.isEmpty()) {
            tile.highlight(Color.YELLOW.deriveColor(1, 1, 1, .25));
            for (Move move : moves) {
                if (controller.moveResultsInCapture(move)) {
                    CaptureMove cMove = (CaptureMove) move;
                    for (Position capturePosition : cMove.getCapturePositions()) {
                        getTileAt(capturePosition).highlight(Color.RED.deriveColor(1, 1, 1, .25));
                    }
                }
                getTileAt(move.getDestination()).highlight(Color.LIGHTGREEN.deriveColor(1, 1, 1, .5));
            }
            hasClicked = true;
        }
    }

    private void secondClick(Tile tile) {
        destPos = tile.getPosition();
        if (destPos.equals(curPos)) {
            getTileAt(destPos).clear();
            for (Move move : moves) {
                if (controller.moveResultsInCapture(move)) {
                    CaptureMove cMove = (CaptureMove) move;
                    for (Position capturePosition : cMove.getCapturePositions()) {
                        getTileAt(capturePosition).clear();
                        if (capturePosition.equals(lastMove.getDestination()) || capturePosition.equals(lastMove.getStart())) {
                            getTileAt(lastMove.getStart()).highlight(Color.ORANGE.deriveColor(1, 1, 1, .25));
                            getTileAt(lastMove.getDestination()).highlight(Color.ORANGE.deriveColor(1, 1, 1, .25));
                        }
                    }
                }
                getTileAt(move.getDestination()).clear();
                if (move.getDestination().equals(lastMove.getDestination()) || move.getDestination().equals(lastMove.getStart())) {
                    getTileAt(lastMove.getStart()).highlight(Color.ORANGE.deriveColor(1, 1, 1, .25));
                    getTileAt(lastMove.getDestination()).highlight(Color.ORANGE.deriveColor(1, 1, 1, .25));
                }
            }
            hasClicked = false;
        } else {
            try {
                controller.makeMove(new NormalMove(curPos, destPos));
                controller.endTurn();
                controller.beginTurn();
                hasClicked = false;
            } catch (IllegalMoveException e) {
            }
        }
    }

    public void updateView(Move moveMade, List<Position> capturedPositions) {
        getTileAt(lastMove.getStart()).clear();
        getTileAt(lastMove.getDestination()).clear();
        if (moves != null) {
            for (Move move : moves) {
                getTileAt(move.getDestination()).clear();
                if (controller.moveResultsInCapture(move)) {
                    CaptureMove cMove = (CaptureMove) move;
                    for (Position capturePosition : cMove.getCapturePositions()) {
                        getTileAt(capturePosition).clear();
                    }
                }
            }
        }
        for (Position position : capturedPositions) {
            getTileAt(position).setSymbol(controller.getSymbolForPieceAt(position));
        }
        getTileAt(moveMade.getStart()).setSymbol(controller.getSymbolForPieceAt(moveMade.getStart()));
        getTileAt(moveMade.getDestination()).setSymbol(controller.getSymbolForPieceAt(moveMade.getDestination()));
        getTileAt(moveMade.getStart()).highlight(Color.ORANGE.deriveColor(1, 1, 1, .25));
        getTileAt(moveMade.getDestination()).highlight(Color.ORANGE.deriveColor(1, 1, 1, .25));
        lastMove = moveMade;
    }

    public void handleGameStateChange(GameState s) {
        stateStatus.setText(s.toString());
        if (s.isGameOver()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Result: " + stateStatus.getText());
            alert.setContentText("Continue playing or exit?");
            ButtonType playAgain = new ButtonType("Continue");
            ButtonType buttonTypeCancel = new ButtonType("Exit");
            alert.getButtonTypes().setAll(playAgain, buttonTypeCancel);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == playAgain) {
                reset(controller.getNewInstance());
            } else {
                System.exit(0);
            }
        }
    }

    public void handleSideChange(Side s) {
        sideStatus.setText(s.toString() + "'s Turn");
    }

    public void reset(GameController newController) {
        controller = newController;
        stateStatus.setText("");
        sideStatus.setText(controller.getCurrentSide() + "'s Turn");
        controller.addMoveListener((Move move, List<Position> capturePositions) ->
                Platform.runLater(
                        () -> updateView(move, capturePositions)));
        controller.addCurrentSideListener((Side side) ->
                Platform.runLater(
                        () -> handleSideChange(side)));
        controller.addGameStateChangeListener((GameState state) ->
                Platform.runLater(
                        () -> handleGameStateChange(state)));
        addPieces();
        controller.startGame();
        hasClicked = false;
    }

    private void addPieces() {
        gridPane.getChildren().clear();
        Map<Piece, Position> pieces = controller.getAllActivePiecesPositions();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = new TileView(new Position(row, col));
                gridPane.add(tile.getRootNode(), 1 + tile.getPosition().getCol(), 1 + tile.getPosition().getRow());
                GridPane.setHgrow(tile.getRootNode(), Priority.ALWAYS);
                GridPane.setVgrow(tile.getRootNode(), Priority.ALWAYS);
                tile.clear();
                tile.setSymbol("");
                tile.getRootNode().setOnMouseClicked(tileListener(tile));
                tiles[row][col] = tile;
            }
        }
        for (Piece p : pieces.keySet()) {
            Position placeAt = pieces.get(p);
            getTileAt(placeAt).setSymbol(p.getType().getSymbol(p.getSide()));
        }
        for (int i = 1; i <= 8; i++) {
            Text coord1 = new Text(" ");
            coord1.setFont(new Font(16));
            GridPane.setHalignment(coord1, HPos.CENTER);
            gridPane.add(coord1, i, 0);

            Text coord2 = new Text(" ");
            coord2.setFont(new Font(16));
            GridPane.setHalignment(coord2, HPos.CENTER);
            gridPane.add(coord2, i, 9);

            Text coord3 = new Text(" ");
            coord3.setFont(new Font(16));
            GridPane.setHalignment(coord3, HPos.CENTER);
            gridPane.add(coord3, 0, i);

            Text coord4 = new Text(" ");
            coord4.setFont(new Font(16));
            GridPane.setHalignment(coord4, HPos.CENTER);
            gridPane.add(coord4, 9, i);
        }
    }

    public Pane getView() {
        return gridPane;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    private Tile getTileAt(int row, int col) {
        return tiles[row][col];
    }

    private Tile getTileAt(Position p) {
        return getTileAt(p.getRow(), p.getCol());
    }
}
