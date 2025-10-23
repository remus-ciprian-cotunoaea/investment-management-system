package com.investment.portfolios.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 *
 * <p>This unchecked exception is intended to be used across the portfolios microservice
 * to signal that an entity or resource requested by the caller does not exist.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
public class NotFoundException extends RuntimeException {

    /**
     * Create a new NotFoundException with a human-readable message describing
     * the missing resource or cause.
     *
     * @param message a descriptive message for the missing resource
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    public NotFoundException(String message) {
        super(message);
    }
}