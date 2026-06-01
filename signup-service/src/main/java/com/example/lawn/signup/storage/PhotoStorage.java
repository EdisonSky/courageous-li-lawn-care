package com.example.lawn.signup.storage;

public interface PhotoStorage {

    StoredPhoto storeSignupPhoto(long signupId, String originalFilename, String contentType, byte[] bytes);

    String urlForKey(String key);

    record StoredPhoto(String key, String url) {
    }
}
