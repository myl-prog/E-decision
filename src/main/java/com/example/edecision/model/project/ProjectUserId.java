package com.example.edecision.model.project;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectUserId implements Serializable {
    @ApiModelProperty(notes = "Identifiant de l'utilisateur", value = "1", required = true)
    private int userId;
    @ApiModelProperty(notes = "Identifiant du projet", value = "1", required = true)
    private int projectId;
}
