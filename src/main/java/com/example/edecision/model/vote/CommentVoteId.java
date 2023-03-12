package com.example.edecision.model.vote;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentVoteId implements Serializable {

    private int userId;
    private int commentId;

    public CommentVoteId(){}
    public CommentVoteId(int userId, int commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }
}
