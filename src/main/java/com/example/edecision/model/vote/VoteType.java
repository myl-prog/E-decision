package com.example.edecision.model.vote;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity(name = "vote_type")
@Table(name = "vote_type")
public class VoteType {
    @Id
    @Column(name = "id")
    @ApiModelProperty(notes = "Identifiant du type", example = "1", required = true)
    private int id;

    @Column(name = "name")
    @ApiModelProperty(notes = "Nom du type", example = "Accepté", required = true)
    private String name;

}
