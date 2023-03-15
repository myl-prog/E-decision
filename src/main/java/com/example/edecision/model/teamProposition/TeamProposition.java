package com.example.edecision.model.teamProposition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "team_proposition")
@IdClass(TeamPropositionId.class)
@Table(name = "team_proposition")
public class TeamProposition {

    @Id
    @Column(name = "team_id")
    @ApiModelProperty(notes = "Identifiant de l'équipe", value = "18", required = true)
    private int teamId;

    @Id
    @Column(name = "proposition_id")
    @ApiModelProperty(notes = "Identifiant de la proposition", value = "7", required = true)
    private int propositionId;

    public TeamProposition(int teamId, int propositionId){
        this.teamId = teamId;
        this.propositionId = propositionId;
    }

    public TeamProposition(){}
}
