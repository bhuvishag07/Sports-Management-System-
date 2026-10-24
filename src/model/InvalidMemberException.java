package model;

/**
 * Custom Exception thrown when member data fails validation or violates rules.
 */
public class InvalidMemberException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidMemberException(String message) {
        super(message);
    }

    public InvalidMemberException(String message, Throwable cause) {
        super(message, cause);
    }
}
