package com.example.edecision.model.authentication;

import com.sun.istack.NotNull;

public class AuthRequest {

    @NotNull
    private String login;

    @NotNull
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
