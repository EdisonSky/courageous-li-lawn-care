package com.example.lawn.signup;

public record PhotoUploadResponse(
        Long signupId,
        String lawnPhotoKey,
        String lawnPhotoUrl
) {
}
