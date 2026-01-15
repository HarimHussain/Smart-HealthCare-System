package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import system.HealthCareSystem;
import ui.Main;
import exceptions.SlotFullException;
import exceptions.InvalidDateException;
import users.Doctor;
import users.Patient;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class BookAppointmentController implements Initializable {

    @FXML private ComboBox<String> specializationComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextArea conditionField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private TableView<DoctorTableRow> doctorsTable;
    @FXML private RadioButton timeSlot9AM;
    @FXML private RadioButton timeSlot11AM;
    @FXML private RadioButton timeSlot2PM;
    @FXML private RadioButton timeSlot4PM;
    @FXML private TextField searchField;
    @FXML private VBox doctorInfoCard;
    @FXML private Label doctorNameLabel;
    @FXML private Label doctorSpecializationLabel;
    @FXML private Label availabilityLabel;

    private HealthCareSystem healthcareSystem;
    private ObservableList<DoctorTableRow> doctorTableData;
    private ToggleGroup timeSlotGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        healthcareSystem = Main.getHealthcareSystem();

        // Initialize specialization combo box
        specializationComboBox.getItems().addAll(
                "General Physician",
                "Dentist",
                "Dermatologist",
                "Cardiologist",
                "Pediatrician"
        );
        specializationComboBox.getSelectionModel().selectFirst();

        // Initialize date picker
        datePicker.setValue(LocalDate.now());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        // Initialize time slot toggle group
        timeSlotGroup = new ToggleGroup();
        timeSlot9AM.setToggleGroup(timeSlotGroup);
        timeSlot11AM.setToggleGroup(timeSlotGroup);
        timeSlot2PM.setToggleGroup(timeSlotGroup);
        timeSlot4PM.setToggleGroup(timeSlotGroup);

        // Initialize doctor table
        initializeDoctorsTable();
        loadDoctors();
    }

    private void initializeDoctorsTable() {
        doctorTableData = FXCollections.observableArrayList();
        doctorsTable.setItems(doctorTableData);
    }

    private void loadDoctors() {
        doctorTableData.clear();
        List<Doctor> doctors = healthcareSystem.getAllDoctors();

        for (Doctor doctor : doctors) {
            DoctorTableRow row = new DoctorTableRow(doctor);
            doctorTableData.add(row);
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadDoctors();
            return;
        }

        doctorTableData.clear();
        List<Doctor> doctors = healthcareSystem.getAllDoctors();

        for (Doctor doctor : doctors) {
            if (doctor.getName().toLowerCase().contains(searchText) ||
                    doctor.getSpecialization().toLowerCase().contains(searchText) ||
                    doctor.getEmail().toLowerCase().contains(searchText)) {
                DoctorTableRow row = new DoctorTableRow(doctor);
                doctorTableData.add(row);
            }
        }
    }

    @FXML
    private void handleSpecializationChange() {
        String specialization = specializationComboBox.getValue();

        if (specialization == null || specialization.isEmpty()) {
            loadDoctors();
        } else {
            List<Doctor> filteredDoctors = healthcareSystem.searchDoctor(specialization);
            doctorTableData.clear();

            for (Doctor doctor : filteredDoctors) {
                DoctorTableRow row = new DoctorTableRow(doctor);
                doctorTableData.add(row);
            }
        }
    }

    @FXML
    private void handleCheckAvailability() {
        // Get selected doctor from table
        DoctorTableRow selectedRow = doctorsTable.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            showError("Please select a doctor from the table first");
            return;
        }

        Doctor selectedDoctor = selectedRow.doctor;
        LocalDate selectedDate = datePicker.getValue();

        if (selectedDate == null) {
            showError("Please select a date");
            return;
        }

        availabilityLabel.setText("Availability check for " +
                selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) +
                ": Most slots available");
        availabilityLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
    }

    @FXML
    private void handleBookAppointment() {
        // Get selected doctor from table
        DoctorTableRow selectedRow = doctorsTable.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            showError("Please select a doctor from the table");
            return;
        }

        Doctor selectedDoctor = selectedRow.doctor;
        LocalDate selectedDate = datePicker.getValue();
        String condition = conditionField.getText().trim();

        // Validation
        if (selectedDate == null) {
            showError("Please select a date");
            return;
        }

        if (condition.isEmpty()) {
            showError("Please describe your medical condition");
            return;
        }

        // Check if a time slot is selected
        RadioButton selectedTimeSlot = (RadioButton) timeSlotGroup.getSelectedToggle();
        if (selectedTimeSlot == null) {
            showError("Please select a time slot");
            return;
        }

        String timeSlot = selectedTimeSlot.getUserData().toString();

        try {
            // For demo, use first patient
            List<Patient> patients = healthcareSystem.getAllPatients();
            if (patients.isEmpty()) {
                showError("No patient found. Please register first.");
                return;
            }

            Patient patient = patients.get(0);

            healthcareSystem.bookAppointment(
                    patient,
                    selectedDoctor,
                    selectedDate.toString(),
                    timeSlot,
                    condition
            );

            showSuccess("Appointment booked successfully!");
            clearForm();

        } catch (SlotFullException e) {
            showError("Selected time slot is not available. Please choose another slot.");
        } catch (InvalidDateException e) {
            showError("Invalid date selected. Please choose a valid date.");
        } catch (Exception e) {
            showError("Failed to book appointment: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        showSuccess("Form cleared.");
    }

    private void clearForm() {
        datePicker.setValue(LocalDate.now());
        conditionField.clear();
        searchField.clear();
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
        doctorInfoCard.setVisible(false);
        doctorsTable.getSelectionModel().clearSelection();

        // Clear time slot selection
        timeSlotGroup.selectToggle(null);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }

    // Inner class for table rows
    public class DoctorTableRow {
        private final SimpleStringProperty name;
        private final SimpleStringProperty specialization;
        private final SimpleStringProperty email;
        private final SimpleStringProperty available;
        private final SimpleObjectProperty<Button> selectButton;
        public final Doctor doctor;

        public DoctorTableRow(Doctor doctor) {
            this.doctor = doctor;
            this.name = new SimpleStringProperty(doctor.getName());
            this.specialization = new SimpleStringProperty(doctor.getSpecialization());
            this.email = new SimpleStringProperty(doctor.getEmail());
            this.available = new SimpleStringProperty("Available");

            Button selectBtn = new Button("Select");
            selectBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 10;");
            selectBtn.setOnAction(e -> {
                doctorNameLabel.setText(doctor.getName());
                doctorSpecializationLabel.setText(doctor.getSpecialization());
                doctorInfoCard.setVisible(true);

                // Auto-select this doctor in table
                doctorsTable.getSelectionModel().select(this);
            });

            this.selectButton = new SimpleObjectProperty<>(selectBtn);
        }

        public String getName() { return name.get(); }
        public String getSpecialization() { return specialization.get(); }
        public String getEmail() { return email.get(); }
        public String getAvailable() { return available.get(); }
        public Button getSelectButton() { return selectButton.get(); }
    }
}