package com.b2b.order.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderItemRequest(

        @NotNull(message = "Product id cannot be null.")
        UUID productId,

        @NotBlank(message = "Product name cannot be blank.")
        @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters.")
        String productName,

        @NotNull(message = "Quantity cannot be null.")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than zero.")
        BigDecimal quantity,

        @NotNull(message = "Unit price cannot be null.")
        @DecimalMin(value = "0.01", message = "Unit price must be greater than zero.")
        BigDecimal unitPrice,

        @NotBlank(message = "Currency cannot be blank.")
        @Size(min = 3, max = 3, message = "Currency must be 3 characters.")
        String currency
) {
}