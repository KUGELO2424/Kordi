package pl.kucharski.Kordi.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    EmailValidator validator = new EmailValidator();

    @Test
    void shouldReturnThatEmailIsValid() {
        assertTrue(validator.test("test@mail"));
    }

    @Test
    void shouldReturnThatEmailIsInvalid() {
        assertAll("Should not validate email",
                () -> assertFalse(validator.test("test@")),
                () -> assertFalse(validator.test("@mail")),
                () -> assertFalse(validator.test("test%mail")),
                () -> assertFalse(validator.test("test@@mail"))
        );
    }

}