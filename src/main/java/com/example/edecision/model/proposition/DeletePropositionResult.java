package com.example.edecision.model.proposition;
import com.example.edecision.model.user.User;

import java.util.List;

public class DeletePropositionResult {
    public boolean deleted;
    public List<User> voteUsers;
    public List<User> notVoteUsers;
}
