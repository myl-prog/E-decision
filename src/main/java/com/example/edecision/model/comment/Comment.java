package com.example.edecision.model.comment;

import com.example.edecision.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name = "comment")
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "Identifiant du commentaire", example = "1", required = true)
    private int id;

    @Column(name = "title")
    @ApiModelProperty(notes = "Titre du commentaire", example = "Remarque négative", required = true)
    private String title;

    @Column(name = "content")
    @ApiModelProperty(notes = "Contenu du commentaire", example = "C'est nul")
    private String content;

    @Column(name = "creation_date")
    @ApiModelProperty(notes = "Date de création du commentaire", example = "2023-03-15T09:34:58", required = true)
    private Date creationDate;

    @Column(name = "last_change_date")
    @ApiModelProperty(notes = "Date de dernière modification du commentaire", example = "2023-03-15T09:34:58", required = true)
    private Date lastChangeDate;

    @JsonIgnore
    @Column(name = "is_escalated")
    @ApiModelProperty(notes = "Commentaire de proposition d'escalade", example = "true", required = true)
    private Boolean isEscalated;

    @JsonIgnore
    @Column(name = "is_deleted")
    @ApiModelProperty(notes = "Commentaire de proposition de suppression", example = "true", required = true)
    private Boolean isDeleted;

    @JsonIgnore
    @Column(name = "proposition_id")
    @ApiModelProperty(notes = "Identifiant de la proposition", example = "13", required = true)
    private int propositionId;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ApiModelProperty(notes = "Utilisateur qui a créé le commentaire", required = true)
    private User user;

    public Comment(){}
    public Comment(int propositionId, String title, String content){
        this.propositionId = propositionId;
        this.title = title;
        this.content = content;
    }
    public Comment(int propositionId, boolean isEscalated, boolean isDeleted, User user){
        this.propositionId = propositionId;
        this.isDeleted = isDeleted;
        this.isEscalated = isEscalated;
        this.user = user;
    }
}
