package com.example.user_service.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email
) {}