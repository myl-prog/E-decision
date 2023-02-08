package com.example.edecision.controller.project;

import com.example.edecision.model.project.Project;
import com.example.edecision.service.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProjectController {
    @Autowired
    public ProjectService service;

    @RequestMapping("/projects")
    public List<Project> getAll(){
        return service.getAll();
    }
}
