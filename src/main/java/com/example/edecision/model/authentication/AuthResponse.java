package com.example.edecision.model.authentication;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AuthResponse {

    @ApiModelProperty(notes = "Login de connexion", example = "Toto", required = true)
    private String login;

    @ApiModelProperty(notes = "Token jwt", example = "841ef652f5[...]", required = true)
    private String accessToken;

    public AuthResponse(String login, String accessToken) {
        this.login = login;
        this.accessToken = accessToken;
    }

}
