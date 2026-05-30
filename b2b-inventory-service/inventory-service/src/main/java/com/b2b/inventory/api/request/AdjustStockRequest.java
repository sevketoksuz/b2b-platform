package com.b2b.inventory.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record AdjustStockRequest(

        @NotNull(message = "Company id cannot be null.")
        UUID companyId,

        @NotNull(message = "New quantity cannot be null.")
        @DecimalMin(value = "0.00", message = "New quantity cannot be negative.")
        @Digits(integer = 17, fraction = 2, message = "New quantity must have at most 17 integer digits and 2 decimal places.")
        BigDecimal newQuantity,

        @Size(max = 255, message = "Reason cannot exceed 255 characters.")
        String reason
) {
}