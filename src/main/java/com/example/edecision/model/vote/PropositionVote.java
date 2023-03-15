package com.example.edecision.model.vote;

import com.example.edecision.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "proposition_vote")
@IdClass(PropositionVoteId.class)
@Table(name = "proposition_vote")
public class PropositionVote {

    @Id
    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ApiModelProperty(notes = "Utilisateur", required = true)
    private User user;

    @Id
    @Column(name = "proposition_id")
    @JsonIgnore
    @ApiModelProperty(notes = "Identifiant de la proposition", value = "1", required = true)
    private int propositionId;

    @Id
    @Column(name = "amendement_id")
    @JsonIgnore
    @ApiModelProperty(notes = "Identifiant de l'amendement", value = "4", required = true)
    private int amendementId;

    @ManyToOne()
    @JoinColumn(name = "vote_type_id", referencedColumnName = "id")
    @ApiModelProperty(notes = "Type de vote", required = true)
    private VoteType voteType;

}
