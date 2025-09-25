package com.investment.common.exception;

import lombok.EqualsAndHashCode;

/**
 * 403 - Access denied
 *
 * @author Remus Ciprian Cotunoaea
 * @since September 16, 2025
 */
@EqualsAndHashCode(callSuper = true)
public class ForbiddenException extends BusinessException{

    public ForbiddenException(String message) {

        super("FORBIDDEN");
    }
}
