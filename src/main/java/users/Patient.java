package users;

import appointments.Appointment;
import java.util.*;

public class Patient extends User {
    private String[] medicalHistory;
    private static int patientCounter = 1000;

    // Constructor overloading
    public Patient(String name) {
        this.id = "P" + (++patientCounter);
        this.name = name;
        this.medicalHistory = new String[0];
    }

    public Patient(String name, String email) {
        this(name);
        this.email = email;
    }

    public Patient(String name, String email, String password) {
        this(name, email);
        this.password = password;
    }

    // Getters and Setters
    public String[] getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String[] medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    // Methods
    @Override
    public void login() {
        System.out.println("Patient " + name + " logged in successfully.");
    }

    @Override
    public boolean validateCredentials(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public void bookAppointment(String doctorId, String date, String timeSlot, String disease) {
        Appointment appointment = new Appointment(this.id, doctorId, date, timeSlot, disease, "Pending");
        appointment.saveToFile();
        System.out.println("Appointment booked successfully!");
    }

    public List<Appointment> viewAppointments() {
        List<Appointment> appointments = Appointment.loadAppointmentsFromFile();
        List<Appointment> patientAppointments = new ArrayList<>();

        for (Appointment app : appointments) {
            if (app.getPatientId().equals(this.id)) {
                patientAppointments.add(app);
            }
        }
        return patientAppointments;
    }

    public void addToMedicalHistory(String record) {
        String[] newHistory = Arrays.copyOf(medicalHistory, medicalHistory.length + 1);
        newHistory[medicalHistory.length] = record;
        medicalHistory = newHistory;
    }
}