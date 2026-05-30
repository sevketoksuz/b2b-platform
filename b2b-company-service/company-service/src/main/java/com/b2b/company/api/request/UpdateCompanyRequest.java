package com.b2b.company.api.request;

import com.b2b.company.domain.enumtype.CompanyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCompanyRequest(

        @NotBlank(message = "Company name cannot be blank.")
        @Size(min = 2, max = 150, message = "Company name must be between 2 and 150 characters.")
        String name,

        @NotBlank(message = "Tax number cannot be blank.")
        @Pattern(regexp = "\\d{10,11}", message = "Tax number must contain 10 or 11 digits.")
        String taxNumber,

        @NotNull(message = "Company type cannot be null.")
        CompanyType companyType
) {
}