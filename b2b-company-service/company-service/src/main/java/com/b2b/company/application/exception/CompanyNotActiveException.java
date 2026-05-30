package com.b2b.company.application.exception;

public class CompanyNotActiveException extends RuntimeException {

    public CompanyNotActiveException(String message) {
        super(message);
    }
}