package com.investment.common.exception;

import lombok.EqualsAndHashCode;

/**
 * 401 - Authentication required
 *
 * @author Remus Ciprian Cotunoaea
 * @since September 16, 2025
 */
@EqualsAndHashCode(callSuper = true)
public class UnauthorizedException extends BusinessException{

    public UnauthorizedException(String message) {
        super("UNAUTHORIZED");
    }
}
