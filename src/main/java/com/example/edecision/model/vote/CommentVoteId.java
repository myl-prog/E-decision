package com.example.edecision.model.vote;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CommentVoteId implements Serializable {

    @ApiModelProperty(notes = "Identifiant de l'utilisateur", value = "1", required = true)
    private int userId;
    @ApiModelProperty(notes = "Identifiant du commentaire", value = "4", required = true)
    private int commentId;

    public CommentVoteId(){}
    public CommentVoteId(int userId, int commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }
}
