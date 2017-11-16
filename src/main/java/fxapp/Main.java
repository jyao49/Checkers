package fxapp;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import controller.*;
import view.*;

public class Main extends Application {
    private GameController controller;
    private Text state;
    private Text sideStatus;
    private BoardView board;
    private VBox root;

    public void start(Stage primaryStage) {
        controller = new CheckerController();
        state = new Text();
        state.setFont(new Font(18));
        sideStatus = new Text();
        sideStatus.setFont(new Font(18));
        board = new BoardView(controller, state, sideStatus);
        Button reset = new Button();
        reset.setText("Reset");
        reset.setOnAction(e -> {
                controller = controller.getNewInstance();
                board.reset(controller);
            });
        root = new VBox();
        root.getChildren().addAll(sideStatus, state, board.getView(), reset);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Checkers");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
