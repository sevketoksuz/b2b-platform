package com.b2b.order.application.exception;

public class InventoryClientException extends RuntimeException {

    public InventoryClientException(String message) {
        super(message);
    }

    public InventoryClientException(String message, Throwable cause) {
        super(message, cause);
    }
}