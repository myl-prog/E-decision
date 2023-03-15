package com.example.edecision.model.proposition;
import com.example.edecision.model.user.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeletePropositionResult {
    @ApiModelProperty(notes = "Booléen qui indique si la proposition est supprimée ou non", value = "true", required = true)
    private boolean deleted;
    @ApiModelProperty(notes = "Liste des utilisateurs qui ont voté pour supprimer la proposition", required = true)
    private List<User> voteUsers;
    @ApiModelProperty(notes = "Liste des utilisateurs qui n'ont pas encore voté pour supprimer la proposition", required = true)
    private List<User> notVoteUsers;
}
