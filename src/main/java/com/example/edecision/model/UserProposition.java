package com.example.edecision.model;
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
    public int user_id;

    @Id
    @Column(name = "proposition_id")
    public int proposition_id;

    public UserProposition(int user_id, int proposition_id){
        this.user_id = user_id;
        this.proposition_id = proposition_id;
    }

    public UserProposition(){}
}
