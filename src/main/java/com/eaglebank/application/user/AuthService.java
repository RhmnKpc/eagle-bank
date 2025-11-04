package com.eaglebank.application.user;

/**
 * Application-level service contract for authenticating users and issuing JWT tokens.
 * <p>
 * Implementations validate credentials, load user details, and issue a signed JWT
 * that can be used to authorize subsequent requests.
 * <p>
 * See {@link UserServiceImpl} for the default implementation.
 */
public interface AuthService {

    /**
     * Authenticates a user using the provided credentials and returns a JWT token
     * encapsulated in an {@link AuthenticationResult}.
     *
     * @param command immutable input carrying the user's email and raw password
     * @return the authentication result including user id, email, and a JWT token
     * @throws com.eaglebank.domain.exception.UserNotFoundException if a user with the given email does not exist
     * @throws IllegalArgumentException                             if the provided credentials are invalid
     */
    AuthenticationResult authenticate(AuthenticationCommand command);

    /**
     * Immutable command carrying user credentials for authentication.
     *
     * @param email    the user's email address (must be a valid email format)
     * @param password the user's raw password
     */
    record AuthenticationCommand(
            String email,
            String password
    ) {
    }

    /**
     * Result of a successful authentication.
     *
     * @param userId the authenticated user's identifier
     * @param email  the authenticated user's email address
     * @param token  a signed JWT token to be used for authorizing API requests
     */
    record AuthenticationResult(
            String userId,
            String email,
            String token
    ) {
    }
}
