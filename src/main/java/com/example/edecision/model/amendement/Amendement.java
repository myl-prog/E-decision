package com.example.edecision.model.amendement;

import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.model.user.User;
import com.example.edecision.model.proposition.PropositionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "proposition_amendement")
@Table(name = "proposition_amendement")
public class Amendement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "Identifiant de l'amendement", example = "1", required = true)
    private int id;

    @Column(name = "title")
    @ApiModelProperty(notes = "Titre de l'amendement", example = "Utiliser Jira plutôt que Trello", required = true)
    private String title;

    @Column(name = "content")
    @ApiModelProperty(notes = "Contenu de l'amendement", example = "Parce que c'est mieux")
    private String content;

    @ManyToOne()
    @JoinColumn(name = "amendement_status_id", referencedColumnName = "id")
    @ApiModelProperty(notes = "Statut de l'amendement", required = true)
    private PropositionStatus amendementStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "proposition_id", referencedColumnName = "id")
    @ApiModelProperty(notes = "Proposition amendée", required = true)
    private Proposition amendProposition;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ApiModelProperty(notes = "Utilisateur créateur de l'amendement", required = true)
    private User user;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(notes = "Amendement modifiable", example = "true", required = true)
    private Boolean isEditable;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(notes = "Amendement votable", example = "false", required = true)
    private Boolean isVotable;
}