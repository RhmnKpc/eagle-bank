package com.eaglebank.interfaces.rest.dto.response;

import com.eaglebank.interfaces.rest.dto.request.AddressDto;

import java.time.OffsetDateTime;

/**
 * Response DTO for User
 */
public record UserResponse(
        String id,
        String name,
        String email,
        String phoneNumber,
        AddressDto address,
        OffsetDateTime createdTimestamp,
        OffsetDateTime updatedTimestamp
) {
}
