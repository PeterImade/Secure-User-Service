package com.example.user_service.users;

public record ErrorResponse(String timestamp, int status, String message, String path) {
}
