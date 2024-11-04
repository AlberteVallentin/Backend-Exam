package dat.exceptions;

// Custom exception class that extends Exception and includes a status code
public class ValidationException extends Exception {
  private final int statusCode; // HTTP status code for the exception

  // Constructor that sets the status code and error message
  public ValidationException(int statusCode, String message) {
    super(message); // Pass message to the superclass (Exception)
    this.statusCode = statusCode;
  }

  // Constructor with default status code 400 (Bad Request)
  public ValidationException(String message) {
    this(400, message); // Default to 400 for validation errors
  }

  // Getter method to retrieve the status code
  public int getStatusCode() {
    return statusCode;
  }
}