package com.example.lawn.signup;

import java.time.Instant;

public record CustomerResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String street,
        String city,
        String state,
        String zip,
        Instant createdAt
) {
}
