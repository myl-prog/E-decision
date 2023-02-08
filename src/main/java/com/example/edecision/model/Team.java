package com.example.edecision.model;
import lombok.Data;
import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "project_id")
    private int project_id;
    @Column(name = "team_type_id")
    private int team_type_id;
    public Team(String name, int project_id, int team_type_id) {
        this.setName(name);
        this.setProject_id(project_id);
        this.setTeam_type_id(team_type_id);
    }
    public Team(){

    }
}
