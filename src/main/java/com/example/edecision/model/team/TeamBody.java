package com.example.edecision.model.team;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TeamBody {
    @ApiModelProperty(notes = "Équipe", required = true)
    private Team team;
    @ApiModelProperty(notes = "Liste des identifiants des utilisateurs de l'équipe", required = true)
    private List<Integer> userIdList;
}
