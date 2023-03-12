package com.example.edecision.model.team;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "user_team")
@IdClass(UserTeamId.class)
@Table(name = "user_team")
public class UserTeam {

    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "team_id")
    private int teamId;

    public UserTeam(int userId, int teamId) {
        this.userId = userId;
        this.teamId = teamId;
    }

    public UserTeam() {
    }
}
