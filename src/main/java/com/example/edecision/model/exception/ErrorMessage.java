package com.example.edecision.model.exception;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ErrorMessage {
    @ApiModelProperty(notes = "Code du statut HTTP de l'erreur", example = "404", required = true)
    private int statusCode;

    @ApiModelProperty(notes = "Date de l'erreur", example = "2023-03-15T09:34:58", required = true)
    private Date timestamp;

    @ApiModelProperty(notes = "Message d'explication de l'erreur", example = "This project doesn't exists", required = true)
    private String message;

    @ApiModelProperty(notes = "Uri de la route appelée", example = "uri=/projects/78", required = true)
    private String description;

    public ErrorMessage(int statusCode, Date timestamp, String message, String description) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.description = description;
    }
}
