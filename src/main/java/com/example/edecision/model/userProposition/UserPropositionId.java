package com.example.edecision.model.userProposition;

import java.io.Serializable;

public class UserPropositionId implements Serializable {
    public int user_id;

    public int proposition_id;

    // default constructor

    public UserPropositionId(int user_id, int proposition_id) {
        this.user_id = user_id;
        this.proposition_id = proposition_id;
    }
    public UserPropositionId(){}
}
