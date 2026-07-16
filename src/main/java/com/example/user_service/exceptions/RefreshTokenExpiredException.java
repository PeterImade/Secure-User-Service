package com.example.user_service.exceptions;

public class RefreshTokenExpiredException extends RuntimeException{
    public RefreshTokenExpiredException(String message)
    {
        super(message);
    }
}
