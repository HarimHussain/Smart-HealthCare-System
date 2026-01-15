package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import system.HealthCareSystem;
import ui.Main;
import appointments.Appointment;
import users.Patient;
import users.User;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class PatientDashboardController implements Initializable {

    @FXML private StackPane contentPane;
    @FXML private Label dashboardTitle;
    @FXML private Label dashboardSubtitle;
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;

    private HealthCareSystem healthcareSystem;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        healthcareSystem = Main.getHealthcareSystem();
        // In real app, you would get current user from session
        loadDashboardContent();
    }

    private void loadDashboardContent() {
        try {
            VBox dashboardContent = new VBox(24);
            dashboardContent.setStyle("-fx-padding: 0;");

            // Stats Section
            HBox statsSection = new HBox(20);
            statsSection.setStyle("-fx-padding: 0 0 24px 0;");

            // Stats Cards
            statsSection.getChildren().addAll(
                    createStatsCard("📅", "Total Appointments", "12", "#3b82f6"),
                    createStatsCard("⏰", "Upcoming", "3", "#10b981"),
                    createStatsCard("👨‍⚕️", "Doctors", "5", "#8b5cf6"),
                    createStatsCard("📋", "Pending", "2", "#f59e0b")
            );

            // Quick Actions
            VBox quickActions = new VBox(16);
            quickActions.getStyleClass().add("card");

            Label actionsTitle = new Label("Quick Actions");
            actionsTitle.getStyleClass().add("card-title");

            GridPane actionsGrid = new GridPane();
            actionsGrid.setHgap(16);
            actionsGrid.setVgap(16);
            actionsGrid.setStyle("-fx-padding: 8px 0;");

            Button bookAppointmentBtn = createActionButton("Book Appointment", "📅",
                    "#3b82f6", e -> handleBookAppointment());
            Button viewAppointmentsBtn = createActionButton("View Appointments", "📋",
                    "#10b981", e -> handleViewAppointments());
            Button findDoctorBtn = createActionButton("Find Doctor", "👨‍⚕️",
                    "#8b5cf6", e -> handleFindDoctor());
            Button medicalHistoryBtn = createActionButton("Medical History", "🏥",
                    "#f59e0b", e -> handleMedicalHistory());

            actionsGrid.add(bookAppointmentBtn, 0, 0);
            actionsGrid.add(viewAppointmentsBtn, 1, 0);
            actionsGrid.add(findDoctorBtn, 0, 1);
            actionsGrid.add(medicalHistoryBtn, 1, 1);

            quickActions.getChildren().addAll(actionsTitle, actionsGrid);

            // Recent Appointments
            VBox recentAppointments = new VBox(16);
            recentAppointments.getStyleClass().add("card");

            Label appointmentsTitle = new Label("Recent Appointments");
            appointmentsTitle.getStyleClass().add("card-title");

            // Sample Table
            TableView<Map<String, String>> appointmentsTable = new TableView<>();
            appointmentsTable.getStyleClass().add("table-view");
            appointmentsTable.setPrefHeight(200);

            TableColumn<Map<String, String>, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().get("date")));

            TableColumn<Map<String, String>, String> doctorCol = new TableColumn<>("Doctor");
            doctorCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().get("doctor")));

            TableColumn<Map<String, String>, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().get("status")));

            appointmentsTable.getColumns().addAll(dateCol, doctorCol, statusCol);

            recentAppointments.getChildren().addAll(appointmentsTitle, appointmentsTable);

            // Chart Section
            VBox chartSection = new VBox(16);
            chartSection.getStyleClass().add("card");

            Label chartTitle = new Label("Appointment Overview");
            chartTitle.getStyleClass().add("card-title");

            PieChart appointmentChart = new PieChart();
            appointmentChart.setPrefHeight(300);
            appointmentChart.setLegendVisible(false);

            // Sample data
            PieChart.Data pendingData = new PieChart.Data("Pending", 25);
            PieChart.Data confirmedData = new PieChart.Data("Confirmed", 50);
            PieChart.Data cancelledData = new PieChart.Data("Cancelled", 25);

            appointmentChart.getData().addAll(pendingData, confirmedData, cancelledData);

            chartSection.getChildren().addAll(chartTitle, appointmentChart);

            // Add all sections
            dashboardContent.getChildren().addAll(
                    statsSection,
                    quickActions,
                    recentAppointments,
                    chartSection
            );

            contentPane.getChildren().setAll(dashboardContent);

        } catch (Exception e) {
            e.printStackTrace();
            showErrorUI(e.getMessage());
        }
    }

    private HBox createStatsCard(String icon, String label, String value, String color) {
        HBox card = new HBox(16);
        card.getStyleClass().add("stats-card");
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px; -fx-padding: 12px; -fx-background-color: " + color + "20; -fx-background-radius: 8px;");

        // Text
        VBox textBox = new VBox(4);
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label labelLabel = new Label(label);
        labelLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        textBox.getChildren().addAll(valueLabel, labelLabel);

        card.getChildren().addAll(iconLabel, textBox);
        return card;
    }

    private Button createActionButton(String text, String icon, String color, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button();
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(80);
        button.setStyle("-fx-background-color: " + color + "50; -fx-background-radius: 8px; " +
                "-fx-border-color: " + color + "30; -fx-border-radius: 8px; " +
                "-fx-border-width: 1px; -fx-text-fill: " + color + "; " +
                "-fx-font-size: 16px; -fx-font-weight: bold;");
        button.setGraphic(new Label(icon));
        button.setContentDisplay(ContentDisplay.TOP);
        button.setGraphicTextGap(8);
        button.setOnAction(handler);
        return button;
    }

    @FXML
    private void handleDashboard() {
        dashboardTitle.setText("Patient Dashboard");
        dashboardSubtitle.setText("Welcome back to your healthcare portal");
        loadDashboardContent();
    }

    @FXML
    private void handleBookAppointment() {
        try {
            dashboardTitle.setText("Book Appointment");
            dashboardSubtitle.setText("Schedule a new appointment with a doctor");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/BookAppointment.fxml"));
            Parent bookAppointmentView = loader.load();
            contentPane.getChildren().setAll(bookAppointmentView);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorUI("Failed to load booking page");
        }
    }

    @FXML
    private void handleViewAppointments() {
        try {
            dashboardTitle.setText("My Appointments");
            dashboardSubtitle.setText("View and manage your appointments");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/ViewAppointments.fxml"));
            Parent viewAppointments = loader.load();
            contentPane.getChildren().setAll(viewAppointments);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorUI("Failed to load appointments");
        }
    }

    @FXML
    private void handleMedicalHistory() {
        dashboardTitle.setText("Medical History");
        dashboardSubtitle.setText("View your medical records and history");
        showMessage("Medical History feature would display your complete medical records here.");
    }

    @FXML
    private void handleProfile() {
        dashboardTitle.setText("Profile Settings");
        dashboardSubtitle.setText("Manage your account and preferences");
        showMessage("Profile management feature would be implemented here.");
    }

    @FXML
    private void handleFindDoctor() {
        dashboardTitle.setText("Find Doctors");
        dashboardSubtitle.setText("Browse and find healthcare specialists");
        showMessage("Doctor search and directory feature would be implemented here.");
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) contentPane.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ui/views/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Smart Healthcare System - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        VBox messageBox = new VBox(16);
        messageBox.getStyleClass().add("card");
        messageBox.setAlignment(javafx.geometry.Pos.CENTER);
        messageBox.setPrefHeight(300);

        Label icon = new Label("💡");
        icon.setStyle("-fx-font-size: 48px;");

        Label title = new Label("Feature Preview");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label content = new Label(message);
        content.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-text-alignment: center;");
        content.setWrapText(true);

        messageBox.getChildren().addAll(icon, title, content);
        contentPane.getChildren().setAll(messageBox);
    }

    private void showErrorUI(String message) {
        VBox errorBox = new VBox(16);
        errorBox.getStyleClass().add("card");
        errorBox.setAlignment(javafx.geometry.Pos.CENTER);
        errorBox.setPrefHeight(300);

        Label icon = new Label("⚠️");
        icon.setStyle("-fx-font-size: 48px;");

        Label title = new Label("Error");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ef4444;");

        Label content = new Label(message);
        content.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-text-alignment: center;");
        content.setWrapText(true);

        errorBox.getChildren().addAll(icon, title, content);
        contentPane.getChildren().setAll(errorBox);
    }
}