package com.example.edecision.model.teamProposition;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "team_proposition")
@IdClass(TeamPropositionId.class)
@Table(name = "team_proposition")
public class TeamProposition {

    @Id
    @Column(name = "team_id")
    private int teamId;

    @Id
    @Column(name = "proposition_id")
    private int propositionId;

    public TeamProposition(int teamId, int propositionId){
        this.teamId = teamId;
        this.propositionId = propositionId;
    }

    public TeamProposition(){}
}
