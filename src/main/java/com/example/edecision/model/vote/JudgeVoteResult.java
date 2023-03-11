package com.example.edecision.model.vote;

import com.example.edecision.model.proposition.Proposition;
import lombok.Data;

import java.io.Serializable;

@Data
public class JudgeVoteResult implements Serializable {
    private Proposition proposition;
}
