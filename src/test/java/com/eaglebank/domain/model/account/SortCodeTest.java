package com.eaglebank.domain.model.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class SortCodeTest {

    @ParameterizedTest
    @ValueSource(strings = {"10-10-10", "20-30-40", "99-99-99"})
    void shouldCreateValidSortCode(String validSortCode) {
        // when
        SortCode sortCode = SortCode.of(validSortCode);

        // then
        assertThat(sortCode).isNotNull();
        assertThat(sortCode.getValue()).isEqualTo(validSortCode);
    }

    @Test
    void shouldCreateDefaultSortCode() {
        // when
        SortCode sortCode = SortCode.defaultSortCode();

        // then
        assertThat(sortCode.getValue()).isEqualTo("10-10-10");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void shouldRejectBlankSortCode(String blank) {
        // when & then
        assertThatThrownBy(() -> SortCode.of(blank))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Sort code cannot be null or empty");
    }

    @Test
    void shouldRejectNullSortCode() {
        // when & then
        assertThatThrownBy(() -> SortCode.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Sort code cannot be null or empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {"101010", "10-10", "10-10-1", "AA-BB-CC", "10:10:10"})
    void shouldRejectInvalidSortCodeFormat(String invalid) {
        // when & then
        assertThatThrownBy(() -> SortCode.of(invalid))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid sort code format");
    }

    @Test
    void shouldHaveValueSemantics() {
        // given
        String code = "10-10-10";
        SortCode sortCode1 = SortCode.of(code);
        SortCode sortCode2 = SortCode.of(code);

        // when & then
        assertThat(sortCode1).isEqualTo(sortCode2);
        assertThat(sortCode1.hashCode()).isEqualTo(sortCode2.hashCode());
    }
}
