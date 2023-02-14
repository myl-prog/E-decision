package com.example.edecision.model.project;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity(name = "project_status")
@Table(name = "project_status")
public class ProjectStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}