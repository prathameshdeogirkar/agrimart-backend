package com.agrimart.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Address {

    private String firstName;
    private String lastName;
    private String mobile;

    private String street;
    private String apartment;
    private String city;
    private String state;
    private String pincode;
}
