package com.ardnaxela.library_management_system.Exceptions;

public class InvalidIsbnFormatException extends RuntimeException{
    public InvalidIsbnFormatException(String message) {
        super(message);
    }
}
