package com.example.edecision.model;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "team")
public class Team {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "project_id")
    private int project_id;
    @Column(name = "team_type_id")
    private int team_type_id;
}
