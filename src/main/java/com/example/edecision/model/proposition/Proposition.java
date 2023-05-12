package com.example.edecision.model.proposition;

import com.example.edecision.model.project.Project;
import com.example.edecision.model.team.Team;
import com.example.edecision.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity(name = "proposition")
@Table(name = "proposition")
public class Proposition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "Identifiant de la proposition", example = "1", required = true)
    private int id;

    @Column(name = "title")
    @ApiModelProperty(notes = "Titre de la proposition", example = "Arrêter les daily", required = true)
    private String title;

    @Column(name = "begin_time")
    @ApiModelProperty(notes = "Date de création de la proposition", example = "2023-03-15T09:34:58", required = true)
    private Date beginTime;

    @Column(name = "end_time")
    @ApiModelProperty(notes = "Date de fin de la proposition", example = "2023-03-23T09:34:58", required = true)
    private Date endTime;

    @Column(name = "amendment_delay")
    @ApiModelProperty(notes = "Délai d'amendement en nombre de jours", example = "5", required = true)
    private int amendmentDelay;

    @Column(name = "content")
    @ApiModelProperty(notes = "Contenu de la proposition", example = "Parce que ça fait perdre du temps")
    private String content;

    @ManyToOne()
    @JoinColumn(name = "proposition_status_id", referencedColumnName = "id")
    @ApiModelProperty(notes = "Statut de la proposition", required = true)
    private PropositionStatus propositionStatus;

    @ManyToOne()
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    @ApiModelProperty(notes = "Projet de la proposition", required = true)
    private Project project;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @OneToMany
    @ApiModelProperty(notes = "Liste d'utilisateurs rattachés à la proposition", required = true)
    private List<User> users;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @OneToMany
    @ApiModelProperty(notes = "Liste des équipes rattachées à la proposition", required = true)
    private List<Team> teams;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(notes = "Proposition modifiable", example = "true", required = true)
    private Boolean isEditable;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(notes = "Proposition votable", example = "false", required = true)
    private Boolean isVotable;
}