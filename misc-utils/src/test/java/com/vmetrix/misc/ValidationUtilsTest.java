package com.vmetrix.misc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test void email_valid()        { assertTrue(ValidationUtils.isValidEmail("user@example.com")); }
    @Test void email_invalid()      { assertFalse(ValidationUtils.isValidEmail("not-an-email")); }
    @Test void email_null()         { assertFalse(ValidationUtils.isValidEmail(null)); }
    @Test void cpf_valid()          { assertTrue(ValidationUtils.isValidCpf("529.982.247-25")); }
    @Test void cpf_invalid()        { assertFalse(ValidationUtils.isValidCpf("111.111.111-11")); }
    @Test void hasLength_ok()       { assertTrue(ValidationUtils.hasLength("hello", 1, 10)); }
    @Test void hasLength_toolong()  { assertFalse(ValidationUtils.hasLength("hello", 1, 3)); }
    @Test void inRange_ok()         { assertTrue(ValidationUtils.inRange(5.0, 1.0, 10.0)); }
    @Test void inRange_out()        { assertFalse(ValidationUtils.inRange(11.0, 1.0, 10.0)); }
}
