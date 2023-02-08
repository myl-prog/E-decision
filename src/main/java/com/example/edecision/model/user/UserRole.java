package com.example.edecision.model.user;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "user_role")
@Table(name = "user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;
}
