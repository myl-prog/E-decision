package com.example.edecision.model.proposition;

import com.example.edecision.model.common.BodyId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PropositionVoteBody {
    @ApiModelProperty(notes = "Identifiant du type de vote", required = true)
    private BodyId voteType;
}
