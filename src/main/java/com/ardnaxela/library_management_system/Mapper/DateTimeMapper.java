package com.ardnaxela.library_management_system.Mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeMapper {

private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static String toString(LocalDateTime dateTime) {
        return dateTime.format(dateTimeFormatter);
    }

    public static LocalDateTime toLocalDateTime(String dateTimeString) {
        return dateTimeString != null ? LocalDateTime.parse(dateTimeString, dateTimeFormatter) : null;
    }
}