package com.eaglebank.domain.model.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserIdTest {

    @Test
    void shouldCreateUserIdFromString() {
        // given
        String id = "usr-123";

        // when
        UserId userId = UserId.of(id);

        // then
        assertThat(userId).isNotNull();
        assertThat(userId.getValue()).isEqualTo(id);
    }

    @Test
    void shouldGenerateRandomUserId() {
        // when
        UserId userId1 = UserId.generate();
        UserId userId2 = UserId.generate();

        // then
        assertThat(userId1).isNotNull();
        assertThat(userId2).isNotNull();
        assertThat(userId1).isNotEqualTo(userId2);
        assertThat(userId1.getValue()).isNotEmpty();
    }

    @Test
    void shouldRejectNullValue() {
        // when & then
        assertThatThrownBy(() -> UserId.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("UserId cannot be null or empty");
    }

    @Test
    void shouldRejectEmptyValue() {
        // when & then
        assertThatThrownBy(() -> UserId.of(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("UserId cannot be null or empty");
    }

    @Test
    void shouldRejectBlankValue() {
        // when & then
        assertThatThrownBy(() -> UserId.of("  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("UserId cannot be null or empty");
    }

    @Test
    void shouldHaveValueSemantics() {
        // given
        String id = "usr-123";
        UserId userId1 = UserId.of(id);
        UserId userId2 = UserId.of(id);

        // when & then
        assertThat(userId1).isEqualTo(userId2);
        assertThat(userId1.hashCode()).isEqualTo(userId2.hashCode());
    }
}
