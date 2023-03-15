package com.example.edecision.model.vote;

import com.example.edecision.model.proposition.Proposition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class JudgeVoteResult implements Serializable {
    @ApiModelProperty(notes = "Proposition", required = true)
    private Proposition proposition;
}
