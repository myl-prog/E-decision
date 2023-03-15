package com.example.edecision.model.team;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserTeamId implements Serializable {
    @ApiModelProperty(notes = "Identifiant de l'utilisateur", value = "1", required = true)
    private int userId;
    @ApiModelProperty(notes = "Identifiant de l'équipe", value = "18", required = true)
    private int teamId;
}
