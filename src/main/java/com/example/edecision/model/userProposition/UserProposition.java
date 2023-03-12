package com.example.edecision.model.userProposition;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "user_proposition")
@IdClass(UserPropositionId.class)
@Table(name = "user_proposition")
public class UserProposition implements Serializable {

    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "proposition_id")
    private int propositionId;

    public UserProposition(int userId, int propositionId){
        this.userId = userId;
        this.propositionId = propositionId;
    }

    public UserProposition(){}
}
