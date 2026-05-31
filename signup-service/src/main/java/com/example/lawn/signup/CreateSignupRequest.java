package com.example.lawn.signup;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreateSignupRequest(
        @Valid @NotNull CreateCustomerRequest customer,
        @NotNull ServiceType serviceType,
        @Positive int lotSizeSqFt,
        @NotNull LocalDate preferredStartDate
) {
}
