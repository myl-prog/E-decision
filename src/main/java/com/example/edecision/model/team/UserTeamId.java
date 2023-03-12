package com.example.edecision.model.team;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserTeamId implements Serializable {
    private int userId;
    private int teamId;
}
