package com.example.edecision.model.vote;

import com.example.edecision.model.user.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PropositionVoteId implements Serializable {

    @ApiModelProperty(notes = "Utilisateur", required = true)
    private User user;
    @ApiModelProperty(notes = "Identifiant de la proposition", value = "1", required = true)
    private int propositionId;
    @ApiModelProperty(notes = "Identifiant de l'amendement", value = "1", required = true)
    private int amendementId;

    public PropositionVoteId(){}
    public PropositionVoteId(User user, int propositionId) {
        this.user = user;
        this.propositionId = propositionId;
    }
    public PropositionVoteId(User user, int propositionId, int amendementId) {
        this.user = user;
        this.propositionId = propositionId;
        this.amendementId = amendementId;
    }
}
