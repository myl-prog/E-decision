package com.example.edecision.model.project;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private String project_id;
    @Column(name = "title")
    private String titile;
    @Column(name = "description")
    private String description;
}
