package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import system.HealthCareSystem;
import ui.Main;
import exceptions.DuplicateEmailException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML private StackPane contentPane;
    @FXML private Label dashboardTitle;
    @FXML private Label dashboardSubtitle;

    private HealthCareSystem healthcareSystem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        healthcareSystem = Main.getHealthcareSystem();
        loadDashboardContent();
    }

    @FXML
    private void handleDashboard() {
        dashboardTitle.setText("Admin Dashboard");
        dashboardSubtitle.setText("Manage the healthcare system");
        loadDashboardContent();
    }

    @FXML
    private void handleManageDoctors() {
        dashboardTitle.setText("Manage Doctors");
        dashboardSubtitle.setText("Add, edit, or remove doctors from the system");
        loadManageDoctorsContent();
    }

    @FXML
    private void handleViewRecords() {
        dashboardTitle.setText("System Records");
        dashboardSubtitle.setText("View all system records and reports");
        loadViewRecordsContent();
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) contentPane.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/ui/views/Login.fxml"));
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Content loading methods
    private void loadDashboardContent() {
        VBox dashboard = new VBox(20);
        dashboard.setStyle("-fx-padding: 20;");

        // Admin stats
        HBox statsSection = new HBox(15);
        statsSection.getChildren().addAll(
                createStatsCard("👨‍⚕️", "Total Doctors", "15", "#3b82f6"),
                createStatsCard("👥", "Total Patients", "120", "#10b981"),
                createStatsCard("📅", "Today's Appointments", "25", "#8b5cf6"),
                createStatsCard("📊", "System Health", "100%", "#f59e0b")
        );

        dashboard.getChildren().add(statsSection);
        contentPane.getChildren().setAll(dashboard);
    }

    private void loadManageDoctorsContent() {
        VBox content = new VBox(20);
        content.getStyleClass().add("card");
        content.setStyle("-fx-padding: 20;");
        content.getChildren().addAll(
                new Label("Doctor Management"),
                new TableView<>(),
                new Button("Edit Doctor"),
                new Button("Remove Doctor")
        );
        contentPane.getChildren().setAll(content);
    }

    private void loadViewRecordsContent() {
        VBox content = new VBox(20);
        content.getStyleClass().add("card");
        content.setStyle("-fx-padding: 20;");
        content.getChildren().addAll(
                new Label("System Records"),
                new Label("Total Appointments: 150"),
                new Label("Total Revenue: $0.00"),
                new Label("System Uptime: 99.9%"),
                new Separator(),
                new Label("Export Options:"),
                new Button("Export Patients"),
                new Button("Export Doctors"),
                new Button("Export Appointments")
        );
        contentPane.getChildren().setAll(content);
    }

    private VBox createStatsCard(String icon, String label, String value, String color) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stats-card");
        card.setStyle("-fx-background-color: " + color + "20; -fx-padding: 15;");

        HBox topRow = new HBox(10);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        topRow.getChildren().addAll(iconLabel, valueLabel);

        Label labelLabel = new Label(label);
        labelLabel.setStyle("-fx-text-fill: #64748b;");

        card.getChildren().addAll(topRow, labelLabel);
        return card;
    }

    // The handleAddDoctor method you already have
    @FXML
    private void handleAddDoctor() {
        // Show add doctor dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Doctor");
        dialog.setHeaderText("Enter doctor details");

        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField specializationField = new TextField();
        specializationField.setPromptText("Specialization");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Specialization:"), 0, 2);
        grid.add(specializationField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    healthcareSystem.addNewDoctor(
                            nameField.getText(),
                            emailField.getText(),
                            "default123", // default password
                            specializationField.getText()
                    );
                    showAlert("Success", "Doctor added successfully!", Alert.AlertType.INFORMATION);
                } catch (DuplicateEmailException e) {
                    showAlert("Error", "Email already exists!", Alert.AlertType.ERROR);
                } catch (Exception e) {
                    showAlert("Error", "Failed to add doctor: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}