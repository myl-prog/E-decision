package com.example.edecision.model.authentication;

import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AuthRequest {

    @NotNull
    @ApiModelProperty(notes = "Login de connexion", example = "Toto", required = true)
    private String login;

    @NotNull
    @ApiModelProperty(notes = "Mot de passe", example = "Toto31@", required = true)
    private String password;

}
