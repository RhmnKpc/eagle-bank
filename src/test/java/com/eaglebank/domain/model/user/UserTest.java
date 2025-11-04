package com.eaglebank.domain.model.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateNewUser() {
        // given
        UserId userId = UserId.generate();
        String name = "John Doe";
        Email email = Email.of("john@example.com");
        PhoneNumber phoneNumber = PhoneNumber.of("+442012345678");
        Address address = Address.of("123 Main St", null, null, "London", "Greater London", "SW1A 1AA");
        String passwordHash = "hashedPassword";

        // when
        User user = User.create(userId, name, email, phoneNumber, address, passwordHash);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getPasswordHash()).isEqualTo(passwordHash);
        assertThat(user.getCreatedTimestamp()).isNotNull();
        assertThat(user.getUpdatedTimestamp()).isNotNull();
    }

    @Test
    void shouldUpdatePersonalInfo() {
        // given
        User user = createTestUser();
        String newName = "Jane Doe";
        PhoneNumber newPhone = PhoneNumber.of("+443012345678");
        Address newAddress = Address.of("456 New St", null, null, "Manchester", "Greater Manchester", "M1 1AA");

        // when
        user.updatePersonalInfo(newName, newPhone, newAddress);

        // then
        assertThat(user.getName()).isEqualTo(newName);
        assertThat(user.getPhoneNumber()).isEqualTo(newPhone);
        assertThat(user.getAddress()).isEqualTo(newAddress);
    }

    @Test
    void shouldRejectNullUserId() {
        // when & then
        assertThatThrownBy(() -> User.create(null, "John", Email.of("test@test.com"),
            PhoneNumber.of("+442012345678"),
            Address.of("1", null, null, "London", "County", "SW1A"), "hash"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectBlankName() {
        // when & then
        assertThatThrownBy(() -> User.create(UserId.generate(), "", Email.of("test@test.com"),
            PhoneNumber.of("+442012345678"),
            Address.of("1", null, null, "London", "County", "SW1A 1AA"), "hash"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be null or empty");
    }

    private User createTestUser() {
        return User.create(
            UserId.generate(),
            "John Doe",
            Email.of("john@example.com"),
            PhoneNumber.of("+442012345678"),
            Address.of("123 Main St", null, null, "London", "Greater London", "SW1A 1AA"),
            "hashedPassword"
        );
    }
}
