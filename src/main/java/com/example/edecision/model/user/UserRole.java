package com.example.edecision.model.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "user_role")
@Table(name = "user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "Identifiant du rôle", example = "1", required = true)
    private int id;

    @Column(name = "name")
    @ApiModelProperty(notes = "Nom du rôle", example = "Standard", required = true)
    private String name;
}
