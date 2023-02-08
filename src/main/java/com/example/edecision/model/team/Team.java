package com.example.edecision.model.team;
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
        this.name = name;
        this.project_id = project_id;
        this.team_type_id = team_type_id;
    }
    public Team(int id, String name, int project_id, int team_type_id) {
        this.id = id;
        this.name = name;
        this.project_id = project_id;
        this.team_type_id = team_type_id;
    }
    public Team(){

    }
}
