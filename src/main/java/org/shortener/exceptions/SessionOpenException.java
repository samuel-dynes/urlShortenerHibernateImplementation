package org.shortener.exceptions;

/**
 * Custom exception class for errors that occur when trying to open a Hibernate session.
 * This exception is typically thrown when there's an issue with the session opening process.
 */
public class SessionOpenException extends Exception {

    /**
     * Constructs a new SessionOpenException with the specified detail message.
     *
     * @param message the detail message.
     */
    public SessionOpenException(String message) {
        super(message);
    }

    /**
     * Constructs a new SessionOpenException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public SessionOpenException(String message, Throwable cause) {
        super(message, cause);
    }
}
