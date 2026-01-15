package system;

import users.*;
import appointments.*;
import database.*;
import exceptions.*;
import java.util.*;

public class HealthCareSystem {
    private List<Patient> patients;
    private List<Doctor> doctors;
    private Admin admin;
    private User currentUser;

    public HealthCareSystem() {
        patients = new ArrayList<>();
        doctors = new ArrayList<>();
        admin = new Admin();
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Load existing data from files
        loadDoctorsFromFile();
        loadPatientsFromFile();

        // If no doctors exist, create sample doctors
        if (doctors.isEmpty()) {
            doctors.add(new Doctor("Dr. Smith", "smith@hospital.com", "pass123", "General Physician"));
            doctors.add(new Doctor("Dr. Johnson", "johnson@hospital.com", "pass123", "Dentist"));
            doctors.add(new Doctor("Dr. Williams", "williams@hospital.com", "pass123", "Dermatologist"));

            // Save sample doctors to file
            for (Doctor doctor : doctors) {
                FileHandler.saveUser(doctor.getId(), doctor.getName(),
                        doctor.getEmail(), doctor.getPassword(),
                        "doctor", doctor.getSpecialization());
            }
        }
    }

    private void loadDoctorsFromFile() {
        List<String[]> doctorData = FileHandler.loadAllUsers("doctor");
        for (String[] data : doctorData) {
            if (data.length >= 5) {
                Doctor doctor = new Doctor(data[1], data[2], data[3], data[4]);
                doctor.setId(data[0]);
                doctors.add(doctor);
            }
        }
    }

    private void loadPatientsFromFile() {
        List<String[]> patientData = FileHandler.loadAllUsers("patient");
        for (String[] data : patientData) {
            if (data.length >= 4) {
                Patient patient = new Patient(data[1], data[2], data[3]);
                patient.setId(data[0]);
                patients.add(patient);
            }
        }
    }

    // Patient registration with exception handling
    public Patient registerPatient(String name, String email, String password)
            throws DuplicateEmailException {

        if (FileHandler.emailExists(email, "patient")) {
            throw new DuplicateEmailException("Email already registered!");
        }

        Patient patient = new Patient(name, email, password);
        patients.add(patient);

        // Save to file
        FileHandler.saveUser(patient.getId(), name, email, password,
                "patient", "No medical history");

        return patient;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    // User login
    public User loginUser(String email, String password, String userType)
            throws UserNotFoundException {

        // Special handling for admin login
        if (userType.equalsIgnoreCase("admin")) {
            Admin admin = new Admin();
            if (admin.validateCredentials(email, password)) {
                admin.login();
                currentUser = admin;  // Set current user
                return admin;
            } else {
                throw new UserNotFoundException("Invalid admin credentials!");
            }
        }

        // For patient and doctor, use FileHandler
        String[] userData = FileHandler.authenticateUser(email, password, userType);

        if (userData == null) {
            throw new UserNotFoundException("Invalid email or password!");
        }

        switch (userType.toLowerCase()) {
            case "patient":
                Patient patient = new Patient(userData[1], userData[2], userData[3]);
                patient.setId(userData[0]);
                patient.login();
                currentUser = patient;  // Set current user
                return patient;

            case "doctor":
                Doctor doctor = new Doctor(userData[1], userData[2], userData[3], userData[4]);
                doctor.setId(userData[0]);
                doctor.login();
                currentUser = doctor;  // Set current user
                return doctor;

            default:
                throw new UserNotFoundException("Invalid user type!");
        }
    }

    // Book appointment with exception handling
    public void bookAppointment(Patient patient, Doctor doctor,
                                String date, String timeSlot, String disease)
            throws SlotFullException, InvalidDateException {

        // Check if date is valid
        if (!isValidDate(date)) {
            throw new InvalidDateException("Invalid date format! Use YYYY-MM-DD");
        }

        // Check if slot is already booked
        List<Appointment> appointments = Appointment.loadAppointmentsFromFile();
        for (Appointment app : appointments) {
            if (app.getDoctorId().equals(doctor.getId()) &&
                    app.getDate().equals(date) &&
                    app.getTimeSlot().equals(timeSlot)) {
                throw new SlotFullException("This time slot is already booked!");
            }
        }

        patient.bookAppointment(doctor.getId(), date, timeSlot, disease);
    }

    private boolean isValidDate(String date) {
        // Basic date validation
        return date != null && date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    // Search doctors with method overloading
    public List<Doctor> searchDoctor(String specialization) {
        return Doctor.searchDoctor(doctors, specialization);
    }

    public List<Doctor> searchDoctor(String specialization, String date) {
        return Doctor.searchDoctor(doctors, specialization, date);
    }

    // Get all appointments for dashboard/chart
    public Map<String, Integer> getAppointmentStats() {
        List<Appointment> appointments = Appointment.loadAppointmentsFromFile();
        Map<String, Integer> stats = new HashMap<>();

        for (Appointment app : appointments) {
            String status = app.getStatus();
            stats.put(status, stats.getOrDefault(status, 0) + 1);
        }

        return stats;
    }

    // Get all doctors
    public List<Doctor> getAllDoctors() {
        return new ArrayList<>(doctors);
    }

    // Get all patients
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    // Add a new doctor (admin function)
    public void addNewDoctor(String name, String email, String password, String specialization)
            throws DuplicateEmailException {

        if (FileHandler.emailExists(email, "doctor")) {
            throw new DuplicateEmailException("Doctor email already exists!");
        }

        admin.addDoctor(name, email, password, specialization);

        // Add to local list
        Doctor newDoctor = new Doctor(name, email, password, specialization);
        doctors.add(newDoctor);
    }

    // Main method for testing
    public static void main(String[] args) {
        HealthCareSystem system = new HealthCareSystem();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Smart Healthcare System ===");
        System.out.println("1. Register Patient");
        System.out.println("2. Login");
        System.out.println("3. Book Appointment");
        System.out.println("4. View Doctors");
        System.out.println("5. Exit");

        boolean running = true;

        while (running) {
            System.out.print("\nEnter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();

                        Patient patient = system.registerPatient(name, email, password);
                        System.out.println("Patient registered successfully! ID: " + patient.getId());
                        break;

                    case 2:
                        System.out.print("Enter email: ");
                        String loginEmail = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String loginPassword = scanner.nextLine();
                        System.out.print("Enter user type (patient/doctor/admin): ");
                        String userType = scanner.nextLine();

                        User user = system.loginUser(loginEmail, loginPassword, userType);
                        System.out.println("Welcome, " + user.getName() + "!");
                        break;

                    case 3:
                        // Simplified appointment booking
                        if (system.getAllPatients().isEmpty()) {
                            System.out.println("No patients found. Please register first.");
                            break;
                        }

                        System.out.println("Available Doctors:");
                        for (Doctor doc : system.getAllDoctors()) {
                            System.out.println(doc.getName() + " - " + doc.getSpecialization());
                        }

                        System.out.print("Enter doctor name: ");
                        String docName = scanner.nextLine();

                        Doctor selectedDoctor = null;
                        for (Doctor doc : system.getAllDoctors()) {
                            if (doc.getName().equalsIgnoreCase(docName)) {
                                selectedDoctor = doc;
                                break;
                            }
                        }

                        if (selectedDoctor != null) {
                            Patient selectedPatient = system.getAllPatients().get(0);
                            System.out.print("Enter date (YYYY-MM-DD): ");
                            String date = scanner.nextLine();
                            System.out.print("Enter time slot (9AM, 11AM, 2PM, 4PM, 6PM): ");
                            String timeSlot = scanner.nextLine();
                            System.out.print("Enter disease: ");
                            String disease = scanner.nextLine();

                            system.bookAppointment(selectedPatient, selectedDoctor, date, timeSlot, disease);
                        } else {
                            System.out.println("Doctor not found!");
                        }
                        break;

                    case 4:
                        System.out.println("\nList of Doctors:");
                        for (Doctor doc : system.getAllDoctors()) {
                            System.out.println("• " + doc.getName() + " (" + doc.getSpecialization() + ")");
                        }
                        break;

                    case 5:
                        running = false;
                        System.out.println("Thank you for using Healthcare System!");
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    public void initializeFromFiles() {
    }
}