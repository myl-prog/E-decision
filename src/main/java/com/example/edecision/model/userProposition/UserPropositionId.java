package com.example.edecision.model.userProposition;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPropositionId implements Serializable {
    private int userId;

    private int propositionId;

    public UserPropositionId(int userId, int propositionId) {
        this.userId = userId;
        this.propositionId = propositionId;
    }
    public UserPropositionId(){}
}
