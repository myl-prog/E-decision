package com.example.edecision.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BodyId {
    @ApiModelProperty(notes = "Identifiant passé dans un body", example = "1", required = true)
    private int id;
}
