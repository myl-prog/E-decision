package com.example.edecision.model.vote;

import java.io.Serializable;

public class CommentVoteId implements Serializable {

    public int user_id;
    public int comment_id;

    public CommentVoteId(){}
    public CommentVoteId(int user_id, int comment_id) {
        this.user_id = user_id;
        this.comment_id = comment_id;
    }
}
