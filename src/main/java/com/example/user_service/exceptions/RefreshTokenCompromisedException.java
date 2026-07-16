package com.example.user_service.exceptions;

public class RefreshTokenCompromisedException extends RuntimeException{
    public RefreshTokenCompromisedException(String message)
    {
        super(message);
    }
}
