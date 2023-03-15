package com.example.edecision.model.team;

import com.example.edecision.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "Identifiant de l'équipe", value = "7", required = true)
    private int id;

    @Column(name = "name")
    @ApiModelProperty(notes = "Nom de l'équipe", value = "Les meilleurs devs", required = true)
    private String name;

    @Nullable
    @Column(name = "project_id")
    @ApiModelProperty(notes = "Identifiant du projet en cours de l'équipe", value = "9", required = true)
    private Integer projectId;

    @ManyToOne()
    @JoinColumn(name = "team_type_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ApiModelProperty(notes = "Type de l'équipe", required = true)
    private TeamType teamType;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ApiModelProperty(notes = "Gestionnaire de l'équipe", required = true)
    private User owner;

    @Transient
    @OneToMany
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(notes = "Liste des utilisateurs de l'équipe", required = true)
    private List<User> users;
}
