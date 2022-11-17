package com.example.edecision.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "login")
    private String login;

    @Column(name = "first_name")
    private String first_name;

    @Column(name = "last_name")
    private String last_name;

    @Column(name = "password")
    private String password;

    @Column(name = "user_role_id")
    private int user_role_id;

    public User(String login, String firstName, String lastName, String password,int userRoleId) {
        setLogin(login);
        setFirst_name(firstName);
        setLast_name(lastName);
        setPassword(password);
        setUser_role_id(userRoleId);

    }
    public User(){

    }
}
