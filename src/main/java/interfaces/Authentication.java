package interfaces;

public interface Authentication {
    boolean validateCredentials(String email, String password);
}
