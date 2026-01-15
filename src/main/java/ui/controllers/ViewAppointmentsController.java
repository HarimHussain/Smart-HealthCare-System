package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import system.HealthCareSystem;
import ui.Main;
import appointments.Appointment;
import users.Doctor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ViewAppointmentsController implements Initializable {

    @FXML private TableView<AppointmentTableRow> appointmentsTable;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker dateFilter;
    @FXML private Label totalLabel;
    @FXML private Label pendingLabel;
    @FXML private Label confirmedLabel;
    @FXML private Label cancelledLabel;

    private HealthCareSystem healthcareSystem;
    private ObservableList<AppointmentTableRow> appointmentsData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        healthcareSystem = Main.getHealthcareSystem();
        appointmentsData = FXCollections.observableArrayList();
        appointmentsTable.setItems(appointmentsData);

        // Initialize status filter
        statusFilter.getItems().addAll("All", "Pending", "Confirmed", "Cancelled");
        statusFilter.getSelectionModel().selectFirst();

        loadAppointments();
        updateStatistics();

        // Set up status column cell factory for styling
        setupStatusColumn();
    }

    private void setupStatusColumn() {
        // Get the status column (5th column)
        TableColumn<AppointmentTableRow, String> statusColumn =
                (TableColumn<AppointmentTableRow, String>) appointmentsTable.getColumns().get(5);

        statusColumn.setCellFactory(column -> new TableCell<AppointmentTableRow, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);

                    // Apply styling based on status
                    switch (status.toLowerCase()) {
                        case "pending":
                            setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                            break;
                        case "confirmed":
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                            break;
                        case "cancelled":
                            setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void loadAppointments() {
        appointmentsData.clear();

        // Get all appointments from system
        List<Appointment> appointments = Appointment.loadAppointmentsFromFile();

        // For demo purposes, we'll show all appointments
        // In real system, filter by current patient
        for (Appointment appointment : appointments) {
            AppointmentTableRow row = new AppointmentTableRow(appointment);
            appointmentsData.add(row);
        }
    }

    @FXML
    private void handleFilter() {
        String status = statusFilter.getValue();
        LocalDate date = dateFilter.getValue();

        if (status == null || "All".equals(status)) {
            loadAppointments();
            return;
        }

        // Filter by status
        ObservableList<AppointmentTableRow> filteredData = FXCollections.observableArrayList();
        for (AppointmentTableRow row : appointmentsData) {
            boolean matchesStatus = row.getStatus().equalsIgnoreCase(status);
            boolean matchesDate = true;

            // If date filter is set, check date
            if (date != null) {
                try {
                    LocalDate rowDate = LocalDate.parse(row.getDate());
                    matchesDate = rowDate.equals(date);
                } catch (Exception e) {
                    matchesDate = false;
                }
            }

            if (matchesStatus && matchesDate) {
                filteredData.add(row);
            }
        }

        appointmentsTable.setItems(filteredData);
        updateStatistics();
    }

    @FXML
    private void handleClearFilter() {
        statusFilter.getSelectionModel().selectFirst();
        dateFilter.setValue(null);
        loadAppointments();
        updateStatistics();
    }

    @FXML
    private void handleRefresh() {
        loadAppointments();
        updateStatistics();
    }

    @FXML
    private void handleCancelAppointment() {
        AppointmentTableRow selectedRow = appointmentsTable.getSelectionModel().getSelectedItem();

        if (selectedRow == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select an appointment to cancel.");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText("Cancel Appointment");
        confirmAlert.setContentText("Are you sure you want to cancel this appointment?\n" +
                "Date: " + selectedRow.getDate() + "\n" +
                "Doctor: " + selectedRow.getDoctor());

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            // Update appointment status to cancelled
            selectedRow.setStatus("Cancelled");
            appointmentsTable.refresh();
            updateStatistics();

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Cancelled");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Appointment cancelled successfully.");
            successAlert.showAndWait();
        }
    }

    @FXML
    private void handleExport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Feature");
        alert.setHeaderText("Export Appointments");
        alert.setContentText("This feature would export appointments to PDF/Excel format.");
        alert.showAndWait();
    }

    private void updateStatistics() {
        ObservableList<AppointmentTableRow> currentData = appointmentsTable.getItems();
        int total = currentData.size();
        int pending = 0;
        int confirmed = 0;
        int cancelled = 0;

        for (AppointmentTableRow row : currentData) {
            switch (row.getStatus().toLowerCase()) {
                case "pending": pending++; break;
                case "confirmed": confirmed++; break;
                case "cancelled": cancelled++; break;
            }
        }

        totalLabel.setText(String.valueOf(total));
        pendingLabel.setText(String.valueOf(pending));
        confirmedLabel.setText(String.valueOf(confirmed));
        cancelledLabel.setText(String.valueOf(cancelled));
    }

    // Inner class for appointment table rows
    public class AppointmentTableRow {
        private final SimpleStringProperty date;
        private final SimpleStringProperty time;
        private final SimpleStringProperty doctor;
        private final SimpleStringProperty specialization;
        private final SimpleStringProperty condition;
        private final SimpleStringProperty status;
        private final SimpleObjectProperty<HBox> actions;

        public AppointmentTableRow(Appointment appointment) {
            this.date = new SimpleStringProperty(appointment.getDate());
            this.time = new SimpleStringProperty(appointment.getTimeSlot());
            this.condition = new SimpleStringProperty(appointment.getDisease());
            this.status = new SimpleStringProperty(appointment.getStatus());

            // Get doctor info (in real system, fetch from database)
            // For now, use placeholder
            List<Doctor> doctors = healthcareSystem.getAllDoctors();
            if (!doctors.isEmpty()) {
                this.doctor = new SimpleStringProperty(doctors.get(0).getName());
                this.specialization = new SimpleStringProperty(doctors.get(0).getSpecialization());
            } else {
                this.doctor = new SimpleStringProperty("Dr. Smith");
                this.specialization = new SimpleStringProperty("General Physician");
            }

            // Create action buttons
            HBox actionBox = new HBox(8);

            Button viewBtn = new Button("View");
            viewBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10;");
            viewBtn.setOnAction(e -> viewAppointmentDetails(appointment));

            Button cancelBtn = new Button("Cancel");
            cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10;");
            cancelBtn.setOnAction(e -> {
                this.status.set("Cancelled");
                // In real system, update in database
                updateStatistics();
            });

            actionBox.getChildren().addAll(viewBtn, cancelBtn);
            this.actions = new SimpleObjectProperty<>(actionBox);
        }

        private void viewAppointmentDetails(Appointment appointment) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Details");
            alert.setHeaderText("Appointment ID: " + appointment.getAppointmentId());
            alert.setContentText("Date: " + appointment.getDate() + "\n" +
                    "Time: " + appointment.getTimeSlot() + "\n" +
                    "Condition: " + appointment.getDisease() + "\n" +
                    "Status: " + appointment.getStatus() + "\n\n" +
                    "Doctor: " + doctor.get() + "\n" +
                    "Specialization: " + specialization.get());
            alert.showAndWait();
        }

        // Getters
        public String getDate() { return date.get(); }
        public String getTime() { return time.get(); }
        public String getDoctor() { return doctor.get(); }
        public String getSpecialization() { return specialization.get(); }
        public String getCondition() { return condition.get(); }
        public String getStatus() { return status.get(); }
        public HBox getActions() { return actions.get(); }

        // Setter for status
        public void setStatus(String status) { this.status.set(status); }
    }
}
