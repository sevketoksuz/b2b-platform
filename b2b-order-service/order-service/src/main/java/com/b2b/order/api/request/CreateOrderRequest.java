package com.b2b.order.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(

        @NotNull(message = "Buyer company id cannot be null.")
        UUID buyerCompanyId,

        @NotNull(message = "Seller company id cannot be null.")
        UUID sellerCompanyId,

        @NotEmpty(message = "Order must contain at least one item.")
        List<@Valid CreateOrderItemRequest> items
) {
}