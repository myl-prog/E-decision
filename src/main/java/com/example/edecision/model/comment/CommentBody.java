package com.example.edecision.model.comment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
public class CommentBody {

    @ApiModelProperty(notes = "Titre du commentaire", example = "Remarque négative", required = true)
    private String title;
    @ApiModelProperty(notes = "Contenu du commentaire", example = "C'est nul")
    private String content;
}
