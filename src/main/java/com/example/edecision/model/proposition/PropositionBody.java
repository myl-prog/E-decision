package com.example.edecision.model.proposition;

import com.example.edecision.model.common.BodyId;
import lombok.Data;

import java.util.List;

@Data
public class PropositionBody {
    private Proposition proposition;
    private BodyId team;
    private List<Integer> userIdList;
}

