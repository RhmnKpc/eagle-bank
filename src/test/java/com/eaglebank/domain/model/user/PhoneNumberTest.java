package com.eaglebank.domain.model.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class PhoneNumberTest {

    @ParameterizedTest
    @ValueSource(strings = {"+442012345678", "+14155551234", "+919876543210"})
    void shouldCreateValidPhoneNumber(String validPhone) {
        // when
        PhoneNumber phoneNumber = PhoneNumber.of(validPhone);

        // then
        assertThat(phoneNumber).isNotNull();
        assertThat(phoneNumber.getValue()).isEqualTo(validPhone);
    }

    @Test
    void shouldNormalizePhoneNumberByRemovingSpacesAndHyphens() {
        // given
        String phoneWithFormatting = "+44 (20) 1234-5678";

        // when
        PhoneNumber phoneNumber = PhoneNumber.of(phoneWithFormatting);

        // then
        assertThat(phoneNumber.getValue()).isEqualTo("+442012345678");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void shouldRejectBlankPhoneNumber(String blankPhone) {
        // when & then
        assertThatThrownBy(() -> PhoneNumber.of(blankPhone))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Phone number cannot be null or empty");
    }

    @Test
    void shouldRejectNullPhoneNumber() {
        // when & then
        assertThatThrownBy(() -> PhoneNumber.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Phone number cannot be null or empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0123456789", "+0123456789", "abcd"})
    void shouldRejectInvalidPhoneNumberFormat(String invalidPhone) {
        // when & then
        assertThatThrownBy(() -> PhoneNumber.of(invalidPhone))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid phone number format");
    }

    @Test
    void shouldHaveValueSemantics() {
        // given
        PhoneNumber phone1 = PhoneNumber.of("+442012345678");
        PhoneNumber phone2 = PhoneNumber.of("+44 (20) 1234-5678");

        // when & then
        assertThat(phone1).isEqualTo(phone2);
        assertThat(phone1.hashCode()).isEqualTo(phone2.hashCode());
    }
}
