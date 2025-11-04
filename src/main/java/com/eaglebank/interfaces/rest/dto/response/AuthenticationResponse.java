package com.eaglebank.interfaces.rest.dto.response;

/**
 * Response DTO for authentication
 */
public record AuthenticationResponse(
        String userId,
        String email,
        String token
) {
}
