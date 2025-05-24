package com.example.File_Image_upload.exceptions;

public class TokenRefreshException extends RuntimeException {
    private final String token;
    private final String message;

    public TokenRefreshException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message));
        this.token = token;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
