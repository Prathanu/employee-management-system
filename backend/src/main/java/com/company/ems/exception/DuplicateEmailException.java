package com.company.ems.exception;

/** Thrown when email already exists */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("Employee with email already exists: " + email);
    }
}
