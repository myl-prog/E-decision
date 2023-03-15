package com.example.edecision.model.project;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProjectBody {
    @ApiModelProperty(notes = "Projet", required = true)
    private Project project;
    @ApiModelProperty(notes = "Équipes qui constituent le projet", required = true)
    private List<Integer> teamIdList;
    @ApiModelProperty(notes = "Utilisateurs du projet avec leurs rôles", required = true)
    private List<ProjectUser> projectUsers;
}
