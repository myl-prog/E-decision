package com.example.edecision.model.project;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity(name = "project_status")
@Table(name = "project_status")
public class ProjectStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "Identifiant du statut", example = "1", required = true)
    private int id;

    @Column(name = "name")
    @ApiModelProperty(notes = "Nom du statut", example = "En cours", required = true)
    private String name;

}