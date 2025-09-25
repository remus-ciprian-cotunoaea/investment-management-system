package com.investment.common.exception;

import lombok.EqualsAndHashCode;

/**
 * Represents a resource-not-found scenario (HTTP 404).
 *
 * @author Remus Ciprian Cotunoaea
 * @since September 16, 2025
 */
@EqualsAndHashCode(callSuper = true)
public class NotFoundException extends BusinessException{

    public NotFoundException(String message) {
        super("NOT_FOUND");
    }
}
