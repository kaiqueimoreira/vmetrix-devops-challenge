package com.vmetrix.misc;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Date and time utility methods.
 */
public final class DateUtils {

    public static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DISPLAY_DATETIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private DateUtils() {}

    /**
     * Returns today's date as an ISO string (yyyy-MM-dd).
     */
    public static String today() {
        return LocalDate.now().format(ISO_DATE);
    }

    /**
     * Formats a LocalDate to dd/MM/yyyy.
     */
    public static String format(LocalDate date) {
        if (date == null) return "";
        return date.format(DISPLAY_FORMAT);
    }

    /**
     * Formats a LocalDateTime to dd/MM/yyyy HH:mm:ss.
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DISPLAY_DATETIME);
    }

    /**
     * Calculates the number of days between two dates (from inclusive, to exclusive).
     */
    public static long daysBetween(LocalDate from, LocalDate to) {
        if (from == null || to == null) return 0;
        return ChronoUnit.DAYS.between(from, to);
    }

    /**
     * Returns true if the given date is in the past.
     */
    public static boolean isPast(LocalDate date) {
        if (date == null) return false;
        return date.isBefore(LocalDate.now());
    }

    /**
     * Returns true if the given date is a weekend day.
     */
    public static boolean isWeekend(LocalDate date) {
        if (date == null) return false;
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    /**
     * Adds the given number of business days (Mon–Fri) to a date.
     */
    public static LocalDate addBusinessDays(LocalDate start, int days) {
        if (start == null) return null;
        LocalDate result = start;
        int added = 0;
        while (added < days) {
            result = result.plusDays(1);
            if (!isWeekend(result)) added++;
        }
        return result;
    }
}
