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
    private int proposition_id;

    @ManyToOne()
    @JoinColumn(name = "vote_type_id", referencedColumnName = "id")
    private VoteType vote_type;

    public PropositionVote(){}
    public PropositionVote(User user, int proposition_id, VoteType vote_type){
        this.user = user;
        this.proposition_id = proposition_id;
        this.vote_type = vote_type;
    }
}
