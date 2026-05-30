package com.b2b.inventory.domain.exception;

public class StockDomainException extends RuntimeException {

    public StockDomainException(String message) {
        super(message);
    }
}