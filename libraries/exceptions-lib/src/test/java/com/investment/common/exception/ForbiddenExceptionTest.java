package com.investment.common.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ForbiddenExceptionTest {

    @Test
    void messageIsSetCorrectly() {
        ForbiddenException ex = new ForbiddenException("FORBIDDEN");
        assertEquals("FORBIDDEN", ex.getMessage());
    }
}