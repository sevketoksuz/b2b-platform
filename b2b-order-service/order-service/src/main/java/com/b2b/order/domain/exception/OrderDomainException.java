package com.b2b.order.domain.exception;

public class OrderDomainException extends RuntimeException {

    public OrderDomainException(String message) {
        super(message);
    }
}