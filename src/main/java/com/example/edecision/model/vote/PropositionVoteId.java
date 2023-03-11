package com.example.edecision.model.vote;

import com.example.edecision.model.user.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class PropositionVoteId implements Serializable {

    private User user;
    private int proposition_id;

    public PropositionVoteId(){}
    public PropositionVoteId(User user, int proposition_id) {
        this.user = user;
        this.proposition_id = proposition_id;
    }
}
