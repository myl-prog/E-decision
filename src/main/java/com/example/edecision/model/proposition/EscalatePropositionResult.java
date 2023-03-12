package com.example.edecision.model.proposition;

import com.example.edecision.model.team.Team;
import com.example.edecision.model.user.User;
import lombok.Data;

import java.util.List;

@Data
public class EscalatePropositionResult {
    private boolean escalated;
    private List<User> voteUsers;
    private List<User> notVoteUsers;
    private List<Team> teams;
}
