package com.example.lawn.signup.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "false", matchIfMissing = true)
public class NoopPhotoStorage implements PhotoStorage {

    @Override
    public StoredPhoto storeSignupPhoto(long signupId, String originalFilename, String contentType, byte[] bytes) {
        throw new UnsupportedOperationException(
                "S3 uploads are disabled. Set aws.s3.enabled=true and S3_UPLOAD_BUCKET for AWS.");
    }

    @Override
    public String urlForKey(String key) {
        return null;
    }
}
