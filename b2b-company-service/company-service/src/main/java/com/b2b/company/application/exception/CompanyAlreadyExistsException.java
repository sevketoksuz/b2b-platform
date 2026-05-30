package com.b2b.company.application.exception;

public class CompanyAlreadyExistsException extends RuntimeException {

    public CompanyAlreadyExistsException(String message) {
        super(message);
    }
}