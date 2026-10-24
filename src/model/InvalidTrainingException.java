package model;

/**
 * Custom Exception thrown when training schedule data is invalid or slot is unavailable.
 */
public class InvalidTrainingException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidTrainingException(String message) {
        super(message);
    }

    public InvalidTrainingException(String message, Throwable cause) {
        super(message, cause);
    }
}
