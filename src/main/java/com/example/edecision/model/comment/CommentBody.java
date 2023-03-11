package com.example.edecision.model.comment;

import lombok.Data;

import javax.persistence.Column;

@Data
public class CommentBody {

    private String title;

    private String content;
}
