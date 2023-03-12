package com.example.edecision.model.vote;

import com.example.edecision.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private User user;

    @Id
    @Column(name = "proposition_id")
    @JsonIgnore
    private int propositionId;

    @Id
    @Column(name = "amendement_id")
    @JsonIgnore
    private int amendementId;

    @ManyToOne()
    @JoinColumn(name = "vote_type_id", referencedColumnName = "id")
    private VoteType voteType;

}
