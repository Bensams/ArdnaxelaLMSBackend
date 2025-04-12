package com.ardnaxela.library_management_system.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeMapper {

    private static final DateTimeFormatter defaultDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static String toString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(defaultDateFormat);
    }

    public static LocalDateTime toLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null; // Handle null or empty input
        }

        // Define an array of supported date formats
        String[] potentialFormats = {
                "M/d/yyyy",       // Single-digit month/day
                "MM/dd/yyyy",     // Double-digit month/day
                "yyyy-MM-dd",     // Typical ISO-8601 format
                "yyyy/MM/dd",     // Alternative ISO format
                "yyyy-MM-dd HH:mm:ss",  // ISO format with time
                "MM/dd/yyyy HH:mm:ss"   // Same format with time
        };

        // Try to parse the input using each format
        for (String format : potentialFormats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                LocalDate localDate = LocalDate.parse(dateTimeString, formatter);

                // Convert LocalDate to LocalDateTime (start of the day)
                return localDate.atStartOfDay();
            } catch (DateTimeParseException ignore) {
                // If parsing fails, try the next format
            }
        }

        // If no format matches, throw an exception
        throw new IllegalArgumentException("Invalid date format: " + dateTimeString);
    }
}