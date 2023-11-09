package com.example.response;

public class AuthResponse extends Response{
    private String token;

    public AuthResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthResponse(String token) {
        this.token = token;
    }

    public AuthResponse(String message, Boolean success, String token) {
        super(message, success);
        this.token = token;
    }
}
