package com.example.edecision.model.vote;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "comment_vote")
@IdClass(CommentVoteId.class)
@Table(name = "comment_vote")
public class CommentVote {
    @Id
    @Column(name = "user_id")
    @ApiModelProperty(notes = "Identifiant de l'utilisateur", value = "1", required = true)
    private int userId;

    @Id
    @Column(name = "comment_id")
    @ApiModelProperty(notes = "Identifiant du commentaire", value = "7", required = true)
    private int commentId;

    @ManyToOne()
    @JoinColumn(name = "vote_type_id", referencedColumnName = "id")
    @ApiModelProperty(notes = "Type du vote", required = true)
    private VoteType voteType;

    public CommentVote(){}
    public CommentVote(int userId, int commentId, VoteType voteType){
        this.userId = userId;
        this.commentId = commentId;
        this.voteType = voteType;
    }
}
