package com.investment.common.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void messageIsSetCorrectly() {
        NotFoundException ex = new NotFoundException("NOT_FOUND");
        assertEquals("NOT_FOUND", ex.getMessage());
    }
}