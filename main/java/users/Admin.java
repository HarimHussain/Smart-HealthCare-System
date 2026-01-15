package users;

import java.io.*;
import java.util.*;
import interfaces.*;

public class Admin extends User {
    private static final String ADMIN_ID = "ADMIN001";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_EMAIL = "admin@healthcare.com";

    public Admin() {
        this.id = ADMIN_ID;
        this.name = "System Administrator";
        this.email = ADMIN_EMAIL;
        this.password = ADMIN_PASSWORD;
    }

    @Override
    public void login() {
        System.out.println("Admin logged in successfully.");
    }

    // Override the validateCredentials method
    @Override
    public boolean validateCredentials(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }


    public void addDoctor(String name, String email, String password, String specialization) {
        Doctor doctor = new Doctor(name, email, password, specialization);
        saveDoctorToFile(doctor);
        System.out.println("Doctor " + name + " added successfully.");
    }

    public List<User> viewAllRecords() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(loadPatientsFromFile());
        allUsers.addAll(loadDoctorsFromFile());
        return allUsers;
    }

    private void saveDoctorToFile(Doctor doctor) {
        try (FileWriter fw = new FileWriter("doctors.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(doctor.getId() + "," + doctor.getName() + "," +
                    doctor.getEmail() + "," + doctor.getPassword() + "," +
                    doctor.getSpecialization());
        } catch (IOException e) {
            System.out.println("Error saving doctor: " + e.getMessage());
        }
    }

    private List<Patient> loadPatientsFromFile() {
        List<Patient> patients = new ArrayList<>();
        File file = new File("patients.txt");

        if (!file.exists()) {
            return patients;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    Patient patient = new Patient(parts[1], parts[2], parts[3]);
                    patient.setId(parts[0]);
                    patients.add(patient);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error loading patients: " + e.getMessage());
        }

        return patients;
    }

    private List<Doctor> loadDoctorsFromFile() {
        List<Doctor> doctors = new ArrayList<>();
        File file = new File("doctors.txt");

        if (!file.exists()) {
            return doctors;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    Doctor doctor = new Doctor(parts[1], parts[2], parts[3], parts[4]);
                    doctor.setId(parts[0]);
                    doctors.add(doctor);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error loading doctors: " + e.getMessage());
        }

        return doctors;
    }
}
