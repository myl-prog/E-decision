package com.example.edecision.model.user;

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

    public UserTeam() {

    }

}
