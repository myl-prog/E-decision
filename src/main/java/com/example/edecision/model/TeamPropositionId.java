package com.example.edecision.model;

import java.io.Serializable;

public class TeamPropositionId implements Serializable {
    public int team_id;

    public int proposition_id;

    // default constructor

    public TeamPropositionId(int team_id, int proposition_id) {
        this.team_id = team_id;
        this.proposition_id = proposition_id;
    }
    public TeamPropositionId(){}
}
