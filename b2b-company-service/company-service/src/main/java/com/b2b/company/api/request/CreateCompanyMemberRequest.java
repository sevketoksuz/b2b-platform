package com.b2b.company.api.request;

import com.b2b.company.domain.enumtype.CompanyMemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCompanyMemberRequest(

        @NotBlank(message = "Full name cannot be blank.")
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters.")
        String fullName,

        @NotBlank(message = "Email cannot be blank.")
        @Email(message = "Email format is invalid.")
        @Size(max = 150, message = "Email cannot exceed 150 characters.")
        String email,

        @NotNull(message = "Role cannot be null.")
        CompanyMemberRole role
) {
}