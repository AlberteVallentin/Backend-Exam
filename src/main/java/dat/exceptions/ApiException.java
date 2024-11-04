package dat.exceptions;

// Custom exception class that extends Exception and includes a status code
public class ApiException extends Exception {

    private final int statusCode; // HTTP status code for the exception

    // Constructor that sets the status code and error message
    public ApiException(int statusCode, String message) {
        super(message); // Pass message to the superclass (Exception)
        this.statusCode = statusCode;
    }

    // Getter method to retrieve the status code
    public int getStatusCode() {
        return statusCode;
    }
}