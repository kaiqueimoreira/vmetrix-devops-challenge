package com.vmetrix.misc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * General-purpose string utility methods.
 */
public final class StringUtils {

    private StringUtils() {}

    /**
     * Reverses the characters of a given string.
     *
     * @param input the string to reverse
     * @return the reversed string, or empty string if input is null
     */
    public static String reverse(String input) {
        if (input == null) return "";
        return new StringBuilder(input).reverse().toString();
    }

    /**
     * Checks whether a string is a palindrome (case-insensitive, ignores spaces).
     *
     * @param input the string to test
     * @return true if the string is a palindrome
     */
    public static boolean isPalindrome(String input) {
        if (input == null) return false;
        String clean = input.toLowerCase().replaceAll("\\s+", "");
        return clean.equals(new StringBuilder(clean).reverse().toString());
    }

    /**
     * Counts the number of words in a string.
     *
     * @param input the input string
     * @return word count, or 0 if input is null or blank
     */
    public static int wordCount(String input) {
        if (input == null || input.isBlank()) return 0;
        return input.trim().split("\\s+").length;
    }

    /**
     * Capitalises the first letter of each word.
     *
     * @param input the input string
     * @return title-cased string
     */
    public static String toTitleCase(String input) {
        if (input == null || input.isBlank()) return "";
        return Arrays.stream(input.trim().split("\\s+"))
                .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    /**
     * Truncates a string to the given max length, appending "..." if truncated.
     *
     * @param input     the string to truncate
     * @param maxLength maximum allowed length (before ellipsis)
     * @return truncated string
     */
    public static String truncate(String input, int maxLength) {
        if (input == null) return "";
        if (maxLength <= 0) return "...";
        if (input.length() <= maxLength) return input;
        return input.substring(0, maxLength) + "...";
    }

    /**
     * Returns a comma-separated list of strings from a List.
     *
     * @param items list of strings
     * @return joined string
     */
    public static String join(List<String> items) {
        if (items == null || items.isEmpty()) return "";
        return String.join(", ", items);
    }
}
