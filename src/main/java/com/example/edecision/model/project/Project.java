package com.example.edecision.model.project;

import com.example.edecision.model.team.Team;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(notes = "Identifiant du projet", example = "1", required = true)
    private int id;

    @Column(name = "title")
    @ApiModelProperty(notes = "Titre du projet", example = "Développement projet E-decision", required = true)
    private String title;

    @Column(name = "description")
    @ApiModelProperty(notes = "Description du projet", example = "Développer une API avec le langage Java et le framework Spring Boot")
    private String description;

    @ManyToOne()
    @JoinColumn(name = "project_status_id", referencedColumnName = "id")
    @ApiModelProperty(notes = "Statut du projet", required = true)
    private ProjectStatus projectStatus;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @OneToMany
    @ApiModelProperty(notes = "Équipes du projet", required = true)
    private List<Team> projectTeams;

}
