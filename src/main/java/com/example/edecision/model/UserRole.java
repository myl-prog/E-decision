package com.example.edecision.model;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "team")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
}
