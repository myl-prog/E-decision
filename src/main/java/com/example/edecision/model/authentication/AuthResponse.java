package com.example.edecision.model.authentication;

public class AuthResponse {
    private String login;
    private String accessToken;

    public AuthResponse() { }

    public AuthResponse(String login, String accessToken) {
        this.login = login;
        this.accessToken = accessToken;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
