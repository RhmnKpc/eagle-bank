package com.eaglebank.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA Embeddable for Address
 */
@Setter
@Getter
@Embeddable
public class AddressEmbeddable {

    private String line1;
    private String line2;
    private String line3;
    private String town;
    private String county;
    private String postcode;

    public AddressEmbeddable() {
    }

    public AddressEmbeddable(String line1, String line2, String line3,
                             String town, String county, String postcode) {
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.town = town;
        this.county = county;
        this.postcode = postcode;
    }
}
