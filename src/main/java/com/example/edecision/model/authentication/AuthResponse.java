package com.example.edecision.model.authentication;

import lombok.Data;

@Data
public class AuthResponse {

    private String login;
    private String accessToken;

    public AuthResponse(String login, String accessToken) {
        this.login = login;
        this.accessToken = accessToken;
    }

}
