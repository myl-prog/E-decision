package com.example.edecision.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "user")
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

    public User(int id, String login, String first_name, String last_name, String password, int user_role_id){
        this.id = id; this.login = login; this.first_name = first_name; this.last_name = last_name; this.password = password; this.user_role_id = user_role_id;
    }

    public User() {

    }
}