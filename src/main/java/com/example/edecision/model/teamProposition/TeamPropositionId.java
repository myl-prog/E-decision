package com.example.edecision.model.teamProposition;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TeamPropositionId implements Serializable {
    @ApiModelProperty(notes = "Identifiant de l'équipe", value = "1", required = true)
    private int teamId;

    @ApiModelProperty(notes = "Identifiant de la proposition", value = "19", required = true)
    private int propositionId;

    public TeamPropositionId(int teamId, int propositionId) {
        this.teamId = teamId;
        this.propositionId = propositionId;
    }
    public TeamPropositionId(){}
}
