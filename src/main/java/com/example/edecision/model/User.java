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

    @ManyToOne(cascade = CascadeType.ALL)
    private UserRole userRole;

    public User(String login, String firstName, String lastName, String password,UserRole userRole) {
        setLogin(login);
        setFirst_name(firstName);
        setLast_name(lastName);
        setPassword(password);
        setUserRole(userRole);
    }
    public User(){

    }
}
