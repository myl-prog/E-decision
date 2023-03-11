package com.example.edecision.model.proposition;

import com.example.edecision.model.team.Team;
import com.example.edecision.model.user.User;

import java.util.List;

public class EscalatePropositionResult {
    public boolean escalated;
    public List<User> voteUsers;
    public List<User> notVoteUsers;
    public List<Team> teams;
}
