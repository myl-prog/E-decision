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
    public int team_id;

    @Id
    @Column(name = "proposition_id")
    public int proposition_id;

    public TeamProposition(int team_id, int proposition_id){
        this.team_id = team_id;
        this.proposition_id = proposition_id;
    }

    public TeamProposition(){}
}
