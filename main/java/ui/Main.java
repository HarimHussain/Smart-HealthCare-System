package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import system.HealthCareSystem;

public class Main extends Application {

    private static HealthCareSystem healthcareSystem;

    public static HealthCareSystem getHealthcareSystem() {
        if (healthcareSystem == null) {
            healthcareSystem = new HealthCareSystem();
        }
        return healthcareSystem;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/Login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/ui/styles/styles.css").toExternalForm());

            primaryStage.setTitle("Smart Healthcare System - Iqra University");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback UI
            showFallbackUI(primaryStage, e);
        }
    }

    private void showFallbackUI(Stage stage, Exception e) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(
                "Error loading UI: " + e.getMessage() +
                        "\n\nApplication will run in basic mode."
        );
        label.setStyle("-fx-font-size: 14px; -fx-padding: 20;");

        Scene scene = new Scene(label, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Smart Healthcare System");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
