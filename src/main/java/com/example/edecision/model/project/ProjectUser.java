package com.example.edecision.model.project;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "project_user")
@IdClass(ProjectUserId.class)
@Table(name = "project_user")
public class ProjectUser {

    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "project_id")
    private int projectId;

    @Column(name = "user_role_id")
    private int userRoleId;

    @Column(name = "is_own")
    private boolean isOwn;

}
