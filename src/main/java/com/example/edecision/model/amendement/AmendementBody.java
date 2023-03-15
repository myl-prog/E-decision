package com.example.edecision.model.amendement;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AmendementBody {
    @ApiModelProperty(notes = "Titre de l'amendement", example = "Utiliser Jira plutôt que Trello", required = true)
    private String title;
    @ApiModelProperty(notes = "Contenu de l'amendement", example = "Parce que c'est mieux")
    private String content;
}
