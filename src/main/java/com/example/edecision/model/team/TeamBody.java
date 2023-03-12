package com.example.edecision.model.team;

import lombok.Data;

import java.util.List;

@Data
public class TeamBody {
    private Team team;
    private List<Integer> userIdList;
}
