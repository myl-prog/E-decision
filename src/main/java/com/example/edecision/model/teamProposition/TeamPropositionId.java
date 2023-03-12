package com.example.edecision.model.teamProposition;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamPropositionId implements Serializable {
    private int teamId;

    private int propositionId;

    // default constructor

    public TeamPropositionId(int teamId, int propositionId) {
        this.teamId = teamId;
        this.propositionId = propositionId;
    }
    public TeamPropositionId(){}
}
