package com.eaglebank.domain.model.user;

import lombok.Getter;
import lombok.Value;

/**
 * Value Object representing a physical Address
 */
@Value
@Getter
public class Address {
    String line1;
    String line2;
    String line3;
    String town;
    String county;
    String postcode;

    private Address(String line1, String line2, String line3,
                    String town, String county, String postcode) {

        if (line1 == null || line1.isBlank()) {
            throw new IllegalArgumentException("Line1 cannot be null or empty");
        }
        if (town == null || town.isBlank()) {
            throw new IllegalArgumentException("Town cannot be null or empty");
        }
        if (county == null || county.isBlank()) {
            throw new IllegalArgumentException("County cannot be null or empty");
        }
        if (postcode == null || postcode.isBlank()) {
            throw new IllegalArgumentException("Postcode cannot be null or empty");
        }

        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.town = town;
        this.county = county;
        this.postcode = postcode;
    }

    public static Address of(String line1, String line2, String line3,
                             String town, String county, String postcode) {
        return new Address(line1, line2, line3, town, county, postcode);
    }
}
