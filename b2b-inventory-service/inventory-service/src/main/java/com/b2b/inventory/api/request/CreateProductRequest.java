package com.b2b.inventory.api.request;

import com.b2b.inventory.domain.enumtype.ProductUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateProductRequest(

        @NotNull(message = "Company id cannot be null.")
        UUID companyId,

        @NotBlank(message = "SKU cannot be blank.")
        @Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters.")
        @Pattern(
                regexp = "^[A-Za-z0-9._-]+$",
                message = "SKU can contain only letters, numbers, dot, underscore and hyphen."
        )
        String sku,

        @NotBlank(message = "Product name cannot be blank.")
        @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters.")
        String name,

        @Size(max = 500, message = "Product description cannot exceed 500 characters.")
        String description,

        @NotNull(message = "Product unit cannot be null.")
        ProductUnit unit
) {
}