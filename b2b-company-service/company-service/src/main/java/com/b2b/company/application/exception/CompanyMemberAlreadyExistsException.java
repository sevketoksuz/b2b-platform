package com.b2b.company.application.exception;

public class CompanyMemberAlreadyExistsException extends RuntimeException {

    public CompanyMemberAlreadyExistsException(String message) {
        super(message);
    }
}