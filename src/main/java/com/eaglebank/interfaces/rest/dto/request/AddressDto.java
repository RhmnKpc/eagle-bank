package com.eaglebank.interfaces.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for Address
 */
public record AddressDto(
        @NotBlank(message = "Line1 is required")
        String line1,

        String line2,

        String line3,

        @NotBlank(message = "Town is required")
        String town,

        @NotBlank(message = "County is required")
        String county,

        @NotBlank(message = "Postcode is required")
        String postcode
) {
}
