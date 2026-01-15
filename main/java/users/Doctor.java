package users;

import interfaces.ReportGenerator;
import appointments.Appointment;
import java.util.*;

public class Doctor extends User implements ReportGenerator {
    private String specialization;
    private boolean[][] schedule; // 7 days x 5 time slots (9AM, 11AM, 2PM, 4PM, 6PM)

    public Doctor(String name, String email, String password, String specialization) {
        this.id = "D" + System.currentTimeMillis();
        this.name = name;
        this.email = email;
        this.password = password;
        this.specialization = specialization;
        this.schedule = new boolean[7][5]; // All slots initially available (false)
    }

    // Getters and Setters
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public boolean[][] getSchedule() { return schedule; }

    // Methods
    @Override
    public void login() {
        System.out.println("Doctor " + name + " logged in successfully.");
    }

    @Override
    public boolean validateCredentials(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public void updateAvailability(int day, int timeSlot, boolean isAvailable) {
        if (day >= 0 && day < 7 && timeSlot >= 0 && timeSlot < 5) {
            schedule[day][timeSlot] = isAvailable;
            System.out.println("Schedule updated for day " + day + ", slot " + timeSlot);
        } else {
            System.out.println("Invalid day or time slot!");
        }
    }

    public boolean isSlotAvailable(int day, int timeSlot) {
        if (day >= 0 && day < 7 && timeSlot >= 0 && timeSlot < 5) {
            return !schedule[day][timeSlot]; // true if available (false in array means available)
        }
        return false;
    }

    public List<Appointment> viewAppointments() {
        List<Appointment> appointments = Appointment.loadAppointmentsFromFile();
        List<Appointment> doctorAppointments = new ArrayList<>();

        for (Appointment app : appointments) {
            if (app.getDoctorId().equals(this.id)) {
                doctorAppointments.add(app);
            }
        }
        return doctorAppointments;
    }

    @Override
    public String generateDailyReport(String date) {
        List<Appointment> appointments = viewAppointments();
        int count = 0;

        for (Appointment app : appointments) {
            if (app.getDate().equals(date)) {
                count++;
            }
        }

        return "Daily Report for " + date + ":\n" +
                "Doctor: " + name + "\n" +
                "Specialization: " + specialization + "\n" +
                "Total Appointments: " + count + "\n";
    }

    // Method overloading for search
    public static List<Doctor> searchDoctor(List<Doctor> doctors, String specialization) {
        List<Doctor> result = new ArrayList<>();
        for (Doctor doc : doctors) {
            if (doc.getSpecialization().equalsIgnoreCase(specialization)) {
                result.add(doc);
            }
        }
        return result;
    }

    public static List<Doctor> searchDoctor(List<Doctor> doctors, String specialization, String date) {
        List<Doctor> result = new ArrayList<>();
        // Simplified implementation - in real system would check schedule
        for (Doctor doc : doctors) {
            if (doc.getSpecialization().equalsIgnoreCase(specialization)) {
                result.add(doc);
            }
        }
        return result;
    }
}