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
    public int user_id;

    @Id
    @Column(name = "project_id")
    public int project_id;

    @Column(name = "user_role_id")
    private int user_role_id;

    @Column(name = "is_own")
    private boolean is_own;

}
