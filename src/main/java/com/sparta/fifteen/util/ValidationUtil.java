package com.sparta.fifteen.util;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ValidationUtil {

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        // Using built-in annotation to validate email format
        return email.matches(EmailRegEx.EMAIL_PATTERN);
    }

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        // Using built-in annotation to validate password complexity
        return password.matches(PasswordRegEx.PASSWORD_PATTERN);
    }

    public static boolean isValidString(String text) {
        return text != null && !text.isEmpty();
    }

    // Inner classes for regex patterns
    private static class EmailRegEx {
        private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    }

    private static class PasswordRegEx {
        private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{10,}$";
    }
}