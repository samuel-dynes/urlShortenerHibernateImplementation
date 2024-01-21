package org.shortener.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionOpenExceptionTest {
    private final static String ERROR_MESSAGE = "Failed to open a session.";

    @Test
    void testConstructorWithMessage() {
        SessionOpenException exception = new SessionOpenException(ERROR_MESSAGE);

        assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new RuntimeException("Underlying cause");
        SessionOpenException exception = new SessionOpenException(ERROR_MESSAGE, cause);

        assertEquals(ERROR_MESSAGE, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
