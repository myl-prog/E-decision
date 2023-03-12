package com.example.edecision.model.proposition;
import com.example.edecision.model.user.User;
import lombok.Data;

import java.util.List;

@Data
public class DeletePropositionResult {
    private boolean deleted;
    private List<User> voteUsers;
    private List<User> notVoteUsers;
}
