package com.example.edecision.model.vote;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "comment_vote")
@IdClass(CommentVoteId.class)
@Table(name = "comment_vote")
public class CommentVote {
    @Id
    @Column(name = "user_id")
    private int user_id;

    @Id
    @Column(name = "comment_id")
    private int comment_id;

    @ManyToOne()
    @JoinColumn(name = "vote_type_id", referencedColumnName = "id")
    private VoteType vote_type;

    public CommentVote(){}
    public CommentVote(int user_id, int comment_id, VoteType vote_type){
        this.user_id = user_id;
        this.comment_id = comment_id;
        this.vote_type = vote_type;
    }
}
