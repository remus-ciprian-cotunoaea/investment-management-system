package com.investment.common.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UnauthorizedExceptionTest {

    @Test
    void messageIsSetCorrectly() {
        UnauthorizedException ex = new UnauthorizedException("UNAUTHORIZED");
        assertEquals("UNAUTHORIZED", ex.getMessage());
    }
}