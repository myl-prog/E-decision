package com.example.edecision.model.authentication;

import com.sun.istack.NotNull;
import lombok.Data;

@Data
public class AuthRequest {

    @NotNull
    private String login;

    @NotNull
    private String password;

}
