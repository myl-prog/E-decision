package com.example.edecision.model.vote;

import com.example.edecision.model.user.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class PropositionVoteId implements Serializable {

    private User user;
    private int propositionId;
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
