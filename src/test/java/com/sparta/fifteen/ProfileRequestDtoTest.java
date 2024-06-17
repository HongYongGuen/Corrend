package com.sparta.fifteen.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidProfileRequestDto() {
        ProfileRequestDto dto = new ProfileRequestDto();
        dto.setName("ValidName123");
        dto.setOneline("Sample oneline");
        dto.setCurrentPassword("ValidPwd123!");
        dto.setNewPassword("StrongPwd123!");
        dto.setCheckNewPassword("StrongPwd123!");

        Set<ConstraintViolation<ProfileRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidProfileRequestDto() {
        ProfileRequestDto dto = new ProfileRequestDto();
        dto.setName("Short");
        dto.setOneline("Sample oneline");
        dto.setCurrentPassword("ValidPwd123!");
        dto.setNewPassword("WeakPwd"); // Invalid: less than 10 characters
        dto.setCheckNewPassword("WeakPwd");

        Set<ConstraintViolation<ProfileRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Password must be at least 10 characters long and include letters, numbers, and special characters.", violations.iterator().next().getMessage());
    }

    @Test
    public void testMismatchedPasswords() {
        ProfileRequestDto dto = new ProfileRequestDto();
        dto.setName("ValidName123");
        dto.setOneline("Sample oneline");
        dto.setCurrentPassword("ValidPwd123!");
        dto.setNewPassword("StrongPwd123!");
        dto.setCheckNewPassword("MismatchedPwd"); // Invalid: does not match new password

        Set<ConstraintViolation<ProfileRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());


    }
}
