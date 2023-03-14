package com.example.edecision.model.proposition;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "proposition_status")
@Table(name = "proposition_status")
public class PropositionStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "Identifiant du statut", example = "2", required = true)
    private int id;

    @Column(name = "name")
    @ApiModelProperty(notes = "Nom du statut", example = "Accepté", required = true)
    private String name;
}