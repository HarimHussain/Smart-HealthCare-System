package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import system.HealthCareSystem;
import ui.Main;
import exceptions.*;
import users.User;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> userTypeComboBox;
    @FXML private Label errorLabel;

    private HealthCareSystem healthcareSystem;

    @FXML
    public void initialize() {
        healthcareSystem = Main.getHealthcareSystem();
        userTypeComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String userType = userTypeComboBox.getValue();

        // Validation
        if (email.isEmpty() || password.isEmpty() || userType == null) {
            showError("Please fill in all fields");
            return;
        }

        try {
            User user = healthcareSystem.loginUser(email, password, userType.toLowerCase());

            // Navigate to appropriate dashboard
            String fxmlPath = "";
            String title = "";

            switch (userType.toLowerCase()) {
                case "patient":
                    fxmlPath = "/ui/views/PatientDashboard.fxml";
                    title = "Patient Dashboard";
                    break;
                case "doctor":
                    fxmlPath = "/ui/views/DoctorDashboard.fxml";
                    title = "Doctor Dashboard";
                    break;
                case "admin":
                    fxmlPath = "/ui/views/AdminDashboard.fxml";
                    title = "Admin Dashboard";
                    break;
            }

            loadDashboard(fxmlPath, title);

        } catch (UserNotFoundException e) {
            showError("Invalid email or password!");
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/Registration.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Patient Registration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void loadDashboard(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ui/styles/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load dashboard");
        }
    }
}