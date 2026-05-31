package com.example.lawn.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Mirrors customer-service JSON for Feign; keep fields aligned with customer API.
 */
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
