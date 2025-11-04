package com.eaglebank.domain.model.user;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * User Aggregate Root
 * <p>
 * Represents a customer of Eagle Bank.
 * This is an aggregate root that encapsulates user identity and personal information.
 */
@Getter
public class User {
    private final UserId id;
    private String name;
    private Email email;
    private PhoneNumber phoneNumber;
    private Address address;
    private String passwordHash;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;

    private User(UserId id, String name, Email email,
                 PhoneNumber phoneNumber, Address address, String passwordHash) {
        if (id == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (phoneNumber == null) {
            throw new IllegalArgumentException("Phone number cannot be null");
        }
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }

        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.passwordHash = passwordHash;
        this.createdTimestamp = LocalDateTime.now();
        this.updatedTimestamp = LocalDateTime.now();
    }

    public static User create(UserId id, String name, Email email,
                              PhoneNumber phoneNumber, Address address, String passwordHash) {
        return new User(id, name, email, phoneNumber, address, passwordHash);
    }

    public static User reconstitute(UserId id, String name, Email email,
                                    PhoneNumber phoneNumber, Address address, String passwordHash,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        User user = new User(id, name, email, phoneNumber, address, passwordHash);
        user.createdTimestamp = createdAt;
        user.updatedTimestamp = updatedAt;
        return user;
    }

    public void updatePersonalInfo(String name, PhoneNumber phoneNumber, Address address) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
        if (address != null) {
            this.address = address;
        }
        this.updatedTimestamp = LocalDateTime.now();
    }

    public void changePassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
        this.passwordHash = newPasswordHash;
        this.updatedTimestamp = LocalDateTime.now();
    }
}
