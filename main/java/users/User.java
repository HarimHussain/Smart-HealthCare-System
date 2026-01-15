package users;

import interfaces.Authentication;

public abstract class User implements Authentication {
    protected String id;
    protected String name;
    protected String email;
    protected String password;

    // Constructors
    public User() {}

    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Abstract methods
    public abstract void login();

    // Interface method implementation
    @Override
    public boolean validateCredentials(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Email: " + email;
    }
}