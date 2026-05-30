package com.b2b.company.application.exception;

public class CompanyMemberNotFoundException extends RuntimeException {

    public CompanyMemberNotFoundException(String message) {
        super(message);
    }
}