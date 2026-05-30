package com.b2b.inventory.application.exception;

public class ProductNotActiveException extends RuntimeException {

    public ProductNotActiveException(String message) {
        super(message);
    }
}