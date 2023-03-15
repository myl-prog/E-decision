package com.example.edecision.model.proposition;

import com.example.edecision.model.team.Team;
import com.example.edecision.model.user.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EscalatePropositionResult {
    @ApiModelProperty(notes = "Booléen qui indique si la proposition a été escaladée ou non", value = "true", required = true)
    private boolean escalated;
    @ApiModelProperty(notes = "Liste des utilisateurs qui ont voté pour escalader la proposition", required = true)
    private List<User> voteUsers;
    @ApiModelProperty(notes = "Liste des utilisateurs qui n'ont pas encore voté pour escalader la proposition", required = true)
    private List<User> notVoteUsers;
    @ApiModelProperty(notes = "Liste des équipes de la proposition", required = true)
    private List<Team> teams;
}
