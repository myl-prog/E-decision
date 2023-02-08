package com.example.edecision.model.user;

import java.io.Serializable;

public class UserTeamId implements Serializable {
    public int user_id;

    public int team_id;

    // default constructor

    public UserTeamId(int user_id, int team_id) {
        this.team_id = team_id;
        this.user_id = user_id;
    }
    public UserTeamId(){}
}
