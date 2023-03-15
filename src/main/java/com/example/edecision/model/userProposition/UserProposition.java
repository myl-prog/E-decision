package com.example.edecision.model.userProposition;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(notes = "Identifiant de l'utilisateur", value = "1", required = true)
    private int userId;

    @Id
    @Column(name = "proposition_id")
    @ApiModelProperty(notes = "Identifiant de la proposition", value = "15", required = true)
    private int propositionId;

    public UserProposition(int userId, int propositionId){
        this.userId = userId;
        this.propositionId = propositionId;
    }

    public UserProposition(){}
}
