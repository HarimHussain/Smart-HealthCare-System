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
import java.net.URL;
import java.util.ResourceBundle;

public class DoctorDashboardController implements Initializable {

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
        dashboardTitle.setText("Doctor Dashboard");
        dashboardSubtitle.setText("Manage your medical practice");
        loadDashboardContent();
    }

    @FXML
    private void handleSchedule() {
        dashboardTitle.setText("My Schedule");
        dashboardSubtitle.setText("Manage your appointment schedule");
        loadScheduleContent();
    }

    @FXML
    private void handlePatients() {
        dashboardTitle.setText("My Patients");
        dashboardSubtitle.setText("View and manage your patients");
        loadPatientsContent();
    }

    @FXML
    private void handleAppointments() {
        dashboardTitle.setText("Today's Appointments");
        dashboardSubtitle.setText("View and manage appointments");
        loadAppointmentsContent();
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

        // Stats cards for doctor
        HBox statsSection = new HBox(15);
        statsSection.getChildren().addAll(
                createStatsCard("📅", "Today's Appointments", "8", "#3b82f6"),
                createStatsCard("⏰", "Available Slots", "12", "#10b981"),
                createStatsCard("👥", "Total Patients", "45", "#8b5cf6"),
                createStatsCard("⭐", "Rating", "4.8", "#f59e0b")
        );

        dashboard.getChildren().add(statsSection);
        contentPane.getChildren().setAll(dashboard);
    }

    private void loadScheduleContent() {
        VBox schedule = new VBox(20);
        schedule.getStyleClass().add("card");
        schedule.setStyle("-fx-padding: 20;");
        schedule.getChildren().addAll(
                new Label("My Schedule"),
                new TableView<>(),
                new Button("Update Schedule")
        );
        contentPane.getChildren().setAll(schedule);
    }

    private void loadPatientsContent() {
        VBox patients = new VBox(20);
        patients.getStyleClass().add("card");
        patients.setStyle("-fx-padding: 20;");
        patients.getChildren().addAll(
                new Label("My Patients"),
                new TableView<>(),
                new Button("View Patient Details")
        );
        contentPane.getChildren().setAll(patients);
    }

    private void loadAppointmentsContent() {
        VBox appointments = new VBox(20);
        appointments.getStyleClass().add("card");
        appointments.setStyle("-fx-padding: 20;");
        appointments.getChildren().addAll(
                new Label("Today's Appointments"),
                new TableView<>(),
                new Button("Mark as Completed")
        );
        contentPane.getChildren().setAll(appointments);
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
}