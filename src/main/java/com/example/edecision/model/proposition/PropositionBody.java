package com.example.edecision.model.proposition;

import com.example.edecision.model.common.BodyId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PropositionBody {
    @ApiModelProperty(notes = "Proposition", required = true)
    private Proposition proposition;
    @ApiModelProperty(notes = "Identifiant de l'équipe chargée de la proposition", required = true)
    private BodyId team;
    @ApiModelProperty(notes = "Liste d'identifiants des utilisateurs gestionnaires de la proposition", required = true)
    private List<Integer> userIdList;
}

