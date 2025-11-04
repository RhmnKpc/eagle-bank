package com.eaglebank.interfaces.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating a user
 */
public record UpdateUserRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotNull(message = "Address is required")
        @Valid
        AddressDto address

) {
}
