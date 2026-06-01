package com.example.lawn.signup.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Locale;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "true")
public class S3PhotoStorage implements PhotoStorage {

    private final S3Client s3Client;
    private final String bucket;
    private final Region region;

    public S3PhotoStorage(
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.region:us-east-1}") String regionName) {
        this.bucket = bucket;
        this.region = Region.of(regionName);
        this.s3Client = S3Client.builder().region(this.region).build();
    }

    @Override
    public StoredPhoto storeSignupPhoto(long signupId, String originalFilename, String contentType, byte[] bytes) {
        String extension = extensionOf(originalFilename);
        String key = "signups/" + signupId + "/" + UUID.randomUUID() + extension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType != null ? contentType : "application/octet-stream")
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(bytes));

        String url = urlForKey(key);
        return new StoredPhoto(key, url);
    }

    @Override
    public String urlForKey(String key) {
        if (key == null) {
            return null;
        }
        return "https://" + bucket + ".s3." + region.id() + ".amazonaws.com/" + key;
    }

    private static String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
    }
}
