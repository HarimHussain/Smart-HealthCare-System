package appointments;

import java.io.*;
import java.util.*;

public class Appointment {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String date;
    private String timeSlot;
    private String disease;
    private String status;

    public Appointment(String patientId, String doctorId, String date,
                       String timeSlot, String disease, String status) {
        this.appointmentId = "APT" + System.currentTimeMillis();
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.disease = disease;
        this.status = status;
    }

    // Getters and Setters
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // File handling methods
    public void saveToFile() {
        try (FileWriter fw = new FileWriter("appointments.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(appointmentId + "," + patientId + "," + doctorId + "," +
                    date + "," + timeSlot + "," + disease + "," + status);
        } catch (IOException e) {
            System.out.println("Error saving appointment: " + e.getMessage());
        }
    }

    public static List<Appointment> loadAppointmentsFromFile() {
        List<Appointment> appointments = new ArrayList<>();
        File file = new File("appointments.txt");

        if (!file.exists()) {
            return appointments;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    Appointment app = new Appointment(parts[1], parts[2], parts[3],
                            parts[4], parts[5], parts[6]);
                    app.appointmentId = parts[0];
                    appointments.add(app);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error loading appointments: " + e.getMessage());
        }

        return appointments;
    }

    @Override
    public String toString() {
        return "Appointment ID: " + appointmentId +
                "\nPatient ID: " + patientId +
                "\nDoctor ID: " + doctorId +
                "\nDate: " + date +
                "\nTime Slot: " + timeSlot +
                "\nDisease: " + disease +
                "\nStatus: " + status + "\n";
    }
}