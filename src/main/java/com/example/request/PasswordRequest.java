package com.example.request;

public class PasswordRequest {
    private String password;
    private String repeatPassword;

    public PasswordRequest() {
    }

    public PasswordRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public PasswordRequest(String password, String repeatPassword) {
        this.password = password;
        this.repeatPassword = repeatPassword;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
