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
    private int userId;

    @Id
    @Column(name = "comment_id")
    private int commentId;

    @ManyToOne()
    @JoinColumn(name = "vote_type_id", referencedColumnName = "id")
    private VoteType voteType;

    public CommentVote(){}
    public CommentVote(int userId, int commentId, VoteType voteType){
        this.userId = userId;
        this.commentId = commentId;
        this.voteType = voteType;
    }
}
