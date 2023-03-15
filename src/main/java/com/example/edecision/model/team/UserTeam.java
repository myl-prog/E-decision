package com.example.edecision.model.team;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "user_team")
@IdClass(UserTeamId.class)
@Table(name = "user_team")
public class UserTeam {

    @Id
    @Column(name = "user_id")
    @ApiModelProperty(notes = "Identifiant de l'utilisateur", value = "1", required = true)
    private int userId;

    @Id
    @Column(name = "team_id")
    @ApiModelProperty(notes = "Identifiant de l'équipe", value = "18", required = true)
    private int teamId;

    public UserTeam(int userId, int teamId) {
        this.userId = userId;
        this.teamId = teamId;
    }

    public UserTeam() {
    }
}
