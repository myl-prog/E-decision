package com.example.edecision.model.project;

import com.example.edecision.model.user.UserRole;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "project")
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne()
    @JoinColumn(name = "project_status_id", referencedColumnName = "id")
    private ProjectStatus project_status;
}
