package com.example.lawn.signup;

import java.time.Instant;
import java.time.LocalDate;

public record SignupResponse(
        Long id,
        Long customerId,
        ServiceType serviceType,
        int lotSizeSqFt,
        LocalDate preferredStartDate,
        SignupStatus status,
        Instant createdAt,
        String lawnPhotoKey,
        String lawnPhotoUrl
) {
}
