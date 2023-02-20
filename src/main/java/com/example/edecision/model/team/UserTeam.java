package com.example.edecision.model.team;

import com.example.edecision.model.user.UserTeamId;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "user_team")
@IdClass(UserTeamId.class)
@Table(name = "user_team")
public class UserTeam {

    @Id
    @Column(name = "user_id")
    private int user_id;

    @Id
    @Column(name = "team_id")
    private int team_id;

    public UserTeam(int userId, int teamId) {
        this.user_id = userId;
        this.team_id = teamId;
    }

    public UserTeam() {
    }
}
