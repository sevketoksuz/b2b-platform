package com.b2b.inventory.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record IncreaseStockRequest(

        @NotNull(message = "Company id cannot be null.")
        UUID companyId,

        @NotNull(message = "Quantity cannot be null.")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than zero.")
        @Digits(integer = 17, fraction = 2, message = "Quantity must have at most 17 integer digits and 2 decimal places.")
        BigDecimal quantity,

        @Size(max = 255, message = "Reason cannot exceed 255 characters.")
        String reason
) {
}