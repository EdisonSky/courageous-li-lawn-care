package com.example.lawn.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank String phone,
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String zip
) {
}
