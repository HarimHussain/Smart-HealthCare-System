package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimpleTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Smart Healthcare System - Simple Test");
        Button button = new Button("Click me!");
        button.setOnAction(e -> label.setText("JavaFX is working!"));

        VBox root = new VBox(20, label, button);
        root.setStyle("-fx-padding: 50; -fx-alignment: center;");

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Test Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}