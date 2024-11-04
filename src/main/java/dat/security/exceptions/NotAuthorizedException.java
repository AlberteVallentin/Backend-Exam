package dat.security.exceptions;

// Class to handle NotAuthorizedException exceptions in the API
public class NotAuthorizedException extends Exception {
    private final int statusCode; // HTTP status code for the exception

    // Primary constructor with status code and message
    public NotAuthorizedException(int statusCode, String message) {
        super(message); // Call to the superclass constructor to set the message
        this.statusCode = statusCode; // Set the HTTP status code
    }

    // Overloaded constructor with status code, message, and cause
    public NotAuthorizedException(int statusCode, String message, Throwable cause) {
        super(message, cause); // Call to the superclass constructor to set the message and cause
        this.statusCode = statusCode; // Set the HTTP status code
    }

    // Getter to retrieve the status code
    public int getStatusCode() {
        return statusCode; // Return the stored status code
    }
}
