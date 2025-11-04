package com.eaglebank.domain.repository;

import com.eaglebank.domain.model.user.Email;
import com.eaglebank.domain.model.user.User;
import com.eaglebank.domain.model.user.UserId;

import java.util.Optional;

/**
 * User Repository Interface (Port)
 * <p>
 * Defines the contract for persisting and retrieving User aggregates.
 * This is a port in the hexagonal architecture - implementations are adapters.
 */
public interface UserRepository {

    /**
     * Saves a user (create or update)
     */
    User save(User user);

    /**
     * Finds a user by ID
     */
    Optional<User> findById(UserId userId);

    /**
     * Finds a user by email
     */
    Optional<User> findByEmail(Email email);

    /**
     * Checks if a user exists by ID
     */
    boolean existsById(UserId userId);

    /**
     * Checks if a user exists by email
     */
    boolean existsByEmail(Email email);

    /**
     * Deletes a user by ID
     */
    void deleteById(UserId userId);
}
