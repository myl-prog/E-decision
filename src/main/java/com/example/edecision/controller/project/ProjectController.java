package com.example.edecision.controller.project;

import com.example.edecision.model.project.Project;
import com.example.edecision.service.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProjectController {
    @Autowired
    public ProjectService projectService;

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects(){
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getAllProjects());
    }
}
