package com.example.edecision.model.userProposition;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserPropositionId implements Serializable {
    @ApiModelProperty(notes = "Identifiant de l'utilisateur", value = "1", required = true)
    private int userId;

    @ApiModelProperty(notes = "Identifiant de la proposition", value = "17", required = true)
    private int propositionId;

    public UserPropositionId(int userId, int propositionId) {
        this.userId = userId;
        this.propositionId = propositionId;
    }
    public UserPropositionId(){}
}
