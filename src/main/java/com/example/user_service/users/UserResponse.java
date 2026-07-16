package com.example.user_service.users;

public record UserResponse(Long id, String name, String email, UserRole role) {
}
