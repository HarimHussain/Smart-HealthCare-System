package database;

import java.io.*;
import java.util.*;

public class FileHandler {

    // Save user to file
    public static void saveUser(String id, String name, String email,
                                String password, String type, String additionalInfo) {
        try (FileWriter fw = new FileWriter(type + "s.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(id + "," + name + "," + email + "," +
                    password + "," + additionalInfo);
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }


    // Check if email exists
    public static boolean emailExists(String email, String type) {
        File file = new File(type + "s.txt");
        if (!file.exists()) {
            return false;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[2].equals(email)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            return false;
        }

        return false;
    }

    // Authenticate user
    public static String[] authenticateUser(String email, String password, String type) {
        File file = new File(type + "s.txt");

        if (!file.exists()) {
            return null;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[2].equals(email) && parts[3].equals(password)) {
                    return parts;
                }
            }
        } catch (FileNotFoundException e) {
            return null;
        }

        return null;
    }

    // Load all users of a specific type
    public static List<String[]> loadAllUsers(String type) {
        List<String[]> users = new ArrayList<>();
        File file = new File(type + "s.txt");

        if (!file.exists()) {
            return users;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                users.add(parts);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }

        return users;
    }
}