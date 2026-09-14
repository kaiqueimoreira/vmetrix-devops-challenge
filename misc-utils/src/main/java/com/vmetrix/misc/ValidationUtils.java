package com.vmetrix.misc;

import java.util.regex.Pattern;

/**
 * Common validation utilities.
 */
public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern CPF_DIGITS = Pattern.compile("\\d{11}");

    private ValidationUtils() {}

    /**
     * Returns true if the email address matches a basic RFC-compatible format.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates a Brazilian CPF number (digits only, 11 chars).
     * Performs digit verification.
     */
    public static boolean isValidCpf(String cpf) {
        if (cpf == null) return false;
        String digits = cpf.replaceAll("[^\\d]", "");
        if (!CPF_DIGITS.matcher(digits).matches()) return false;
        if (digits.chars().distinct().count() == 1) return false; // all same digit

        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (digits.charAt(i) - '0') * (10 - i);
        int first = (sum * 10 % 11) % 10;
        if (first != (digits.charAt(9) - '0')) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) sum += (digits.charAt(i) - '0') * (11 - i);
        int second = (sum * 10 % 11) % 10;
        return second == (digits.charAt(10) - '0');
    }

    /**
     * Returns true if the string is non-null and has length within [min, max].
     */
    public static boolean hasLength(String value, int min, int max) {
        if (value == null) return false;
        int len = value.length();
        return len >= min && len <= max;
    }

    /**
     * Returns true if value is within [min, max] inclusive.
     */
    public static boolean inRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
}
