package com.example.lawn.signup;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class SignupService {

    private final CustomerClient customerClient;
    private final SignupRepository signupRepository;

    public SignupService(CustomerClient customerClient, SignupRepository signupRepository) {
        this.customerClient = customerClient;
        this.signupRepository = signupRepository;
    }

    @Transactional
    public SignupResponse create(CreateSignupRequest request) {
        CustomerResponse customer = customerClient.createCustomer(request.customer());
        Signup signup = Signup.builder()
                .customerId(customer.id())
                .serviceType(request.serviceType())
                .lotSizeSqFt(request.lotSizeSqFt())
                .preferredStartDate(request.preferredStartDate())
                .status(SignupStatus.PENDING)
                .createdAt(Instant.now())
                .build();
        Signup saved = signupRepository.save(signup);
        return toResponse(saved);
    }

    private static SignupResponse toResponse(Signup s) {
        return new SignupResponse(
                s.getId(),
                s.getCustomerId(),
                s.getServiceType(),
                s.getLotSizeSqFt(),
                s.getPreferredStartDate(),
                s.getStatus(),
                s.getCreatedAt()
        );
    }
}
