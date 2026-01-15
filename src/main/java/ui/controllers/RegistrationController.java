package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import system.HealthCareSystem;
import ui.Main;
import exceptions.DuplicateEmailException;
import users.Patient;

import java.io.IOException;

public class RegistrationController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextArea medicalHistoryField;
    @FXML private Label errorLabel;

    private HealthCareSystem healthcareSystem;

    @FXML
    public void initialize() {
        healthcareSystem = Main.getHealthcareSystem();
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String medicalHistory = medicalHistoryField.getText().trim();

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all required fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Please enter a valid email address");
            return;
        }

        try {
            Patient patient = healthcareSystem.registerPatient(name, email, password);

            if (!medicalHistory.isEmpty()) {
                patient.addToMedicalHistory(medicalHistory);
            }

            // Show success message and redirect to login
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("Patient registered successfully!\nYour Patient ID: " + patient.getId());
            alert.showAndWait();

            handleBackToLogin();

        } catch (DuplicateEmailException e) {
            showError("Email already registered. Please use a different email.");
        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/views/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Smart Healthcare System - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
