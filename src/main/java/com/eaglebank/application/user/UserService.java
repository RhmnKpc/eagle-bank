package com.eaglebank.application.user;

import com.eaglebank.domain.model.user.User;
import org.springframework.security.core.Authentication;

/**
 * Application-level service contract for managing {@link User} accounts.
 * <p>
 * Implementations coordinate domain operations and enforce access control for:
 * <ul>
 *   <li>Creating a new user</li>
 *   <li>Fetching a user's own profile</li>
 *   <li>Updating a user's own profile</li>
 *   <li>Deleting a user (only when no bank accounts exist)</li>
 * </ul>
 * See {@link UserServiceImpl} for the default implementation.
 */
public interface UserService {

    /**
     * Creates and persists a new user.
     *
     * @param command immutable input carrying personal details, address, contact info, and password
     * @return the created {@link User}
     * @throws IllegalArgumentException if the email already exists or provided fields are invalid
     */
    User create(CreateUserCommand command);

    /**
     * Retrieves the profile for the specified user, verifying the requester is the same user.
     *
     * @param userId         the id of the user to fetch
     * @param authentication Spring Security authentication containing the requesting user's id
     * @return the {@link User}
     * @throws com.eaglebank.domain.exception.UserNotFoundException       if the user does not exist
     * @throws com.eaglebank.domain.exception.UnauthorizedAccessException if the requester is not the user
     */
    User get(String userId, Authentication authentication);

    /**
     * Deletes the specified user after verifying ownership and ensuring there are no bank accounts.
     *
     * @param userId         the id of the user to delete
     * @param authentication Spring Security authentication containing the requesting user's id
     * @throws com.eaglebank.domain.exception.UserNotFoundException       if the user does not exist
     * @throws com.eaglebank.domain.exception.UnauthorizedAccessException if the requester is not the user
     * @throws com.eaglebank.domain.exception.UserHasAccountsException    if the user still has bank accounts
     */
    void delete(String userId, Authentication authentication);

    /**
     * Updates personal information for the specified user after verifying ownership.
     *
     * @param command        immutable input carrying user id and the fields to update (name, phone, address)
     * @param authentication Spring Security authentication containing the requesting user's id
     * @return the updated {@link User}
     * @throws com.eaglebank.domain.exception.UserNotFoundException       if the user does not exist
     * @throws com.eaglebank.domain.exception.UnauthorizedAccessException if the requester is not the user
     * @throws IllegalArgumentException                                   if provided fields are invalid
     */
    User update(UpdateUserCommand command, Authentication authentication);

    /**
     * Immutable command used to update user information. Omitted fields will not be changed.
     *
     * @param userId      the id of the user to update
     * @param name        new full name (optional)
     * @param email       not used for updates in current flow (reserved for future use)
     * @param phoneNumber new phone number (optional)
     * @param line1       address line 1 (optional, required with town/county/postcode to update address)
     * @param line2       address line 2 (optional)
     * @param line3       address line 3 (optional)
     * @param town        town/city (optional, required with line1/county/postcode to update address)
     * @param county      county (optional, required with line1/town/postcode to update address)
     * @param postcode    postcode (optional, required with line1/town/county to update address)
     */
    record UpdateUserCommand(
            String userId,
            String name,
            String email,
            String phoneNumber,
            String line1,
            String line2,
            String line3,
            String town,
            String county,
            String postcode
    ) {
    }

    /**
     * Immutable command used to create a new user.
     *
     * @param name        full name of the user
     * @param email       unique email address
     * @param phoneNumber phone number in supported format
     * @param line1       address line 1
     * @param line2       address line 2 (optional)
     * @param line3       address line 3 (optional)
     * @param town        town/city
     * @param county      county
     * @param postcode    postcode
     * @param password    raw password that will be hashed before persistence
     */
    record CreateUserCommand(
            String name,
            String email,
            String phoneNumber,
            String line1,
            String line2,
            String line3,
            String town,
            String county,
            String postcode,
            String password
    ) {
    }
}
