package com.example.edecision.model.project;

import com.example.edecision.model.team.Team;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity(name = "project")
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne()
    @JoinColumn(name = "project_status_id", referencedColumnName = "id")
    private ProjectStatus project_status;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @OneToMany
    private List<Team> projectTeams;
}
