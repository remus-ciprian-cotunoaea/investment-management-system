package com.investment.common.exception;

/**
 * Base class for business-related exceptions.
 *
 * @author Remus Ciprian Cotunoaea
 * @since September 16, 2025
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
