package com.investment.common.exception;

import lombok.EqualsAndHashCode;

/**
 * 400 - Invalid request
 *
 * @author Remus Ciprian Cotunoaea
 * @since September 16, 2025
 */
@EqualsAndHashCode(callSuper = true)
public class BadRequestException extends BusinessException{

    public BadRequestException(String message) {
        super("BAD_REQUEST");
    }
}