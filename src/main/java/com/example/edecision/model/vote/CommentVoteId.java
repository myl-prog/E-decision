package com.example.edecision.model.vote;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentVoteId implements Serializable {

    private int user_id;
    private int comment_id;

    public CommentVoteId(){}
    public CommentVoteId(int user_id, int comment_id) {
        this.user_id = user_id;
        this.comment_id = comment_id;
    }
}
