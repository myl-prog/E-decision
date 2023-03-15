package com.example.edecision.model.project;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "project_user")
@IdClass(ProjectUserId.class)
@Table(name = "project_user")
public class ProjectUser {

    @Id
    @Column(name = "user_id")
    @ApiModelProperty(notes = "Identifiant de l'utilisateur", value = "1", required = true)
    private int userId;

    @Id
    @Column(name = "project_id")
    @ApiModelProperty(notes = "Identifiant du projet", value = "2", required = true)
    private int projectId;

    @Column(name = "user_role_id")
    @ApiModelProperty(notes = "Identifiant du rôle de l'utilisateur dans ce projet", value = "3", required = true)
    private int userRoleId;

    @Column(name = "is_own")
    @ApiModelProperty(notes = "Est le gestionnaire du projet", value = "true", required = true)
    private boolean isOwn;

}
