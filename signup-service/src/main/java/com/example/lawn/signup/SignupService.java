package com.example.lawn.signup;

import com.example.lawn.signup.storage.PhotoStorage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
public class SignupService {

    private final CustomerClient customerClient;
    private final SignupRepository signupRepository;
    private final PhotoStorage photoStorage;

    public SignupService(
            CustomerClient customerClient,
            SignupRepository signupRepository,
            PhotoStorage photoStorage) {
        this.customerClient = customerClient;
        this.signupRepository = signupRepository;
        this.photoStorage = photoStorage;
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
        return toResponse(saved, null);
    }

    public SignupResponse getById(Long id) {
        Signup signup = signupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Signup not found"));
        return toResponse(signup, photoStorage.urlForKey(signup.getLawnPhotoKey()));
    }

    @Transactional
    public PhotoUploadResponse uploadPhoto(
            Long id, String originalFilename, String contentType, byte[] bytes) {
        Signup signup = signupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Signup not found"));

        PhotoStorage.StoredPhoto stored = photoStorage.storeSignupPhoto(
                signup.getId(), originalFilename, contentType, bytes);

        signup.setLawnPhotoKey(stored.key());
        signupRepository.save(signup);

        return new PhotoUploadResponse(signup.getId(), stored.key(), stored.url());
    }

    private static SignupResponse toResponse(Signup s, String lawnPhotoUrl) {
        return new SignupResponse(
                s.getId(),
                s.getCustomerId(),
                s.getServiceType(),
                s.getLotSizeSqFt(),
                s.getPreferredStartDate(),
                s.getStatus(),
                s.getCreatedAt(),
                s.getLawnPhotoKey(),
                lawnPhotoUrl
        );
    }
}
