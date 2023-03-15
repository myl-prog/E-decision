package com.example.edecision.model.team;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "team_type")
@Table(name = "team_type")
public class TeamType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "Identifiant du type", example = "1", required = true)
    private int id;

    @Column(name = "name")
    @ApiModelProperty(notes = "Nom du type", example = "Développeur", required = true)
    private String name;
}
