package dat.security.exceptions;

public class SecurityValidationException extends Exception {
    private final int statusCode;

    public SecurityValidationException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    // Constructor with default status code 400 (Bad Request)
    public SecurityValidationException(String message) {
        this(400, message); // Default to 400 for validation errors
    }

    // Constructor that includes both status code and cause
    public SecurityValidationException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}