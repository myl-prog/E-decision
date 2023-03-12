package com.example.edecision.model.team;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "team_type")
@Table(name = "team_type")
public class TeamType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;
}
