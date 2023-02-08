package com.example.edecision.service.project;
import com.example.edecision.model.project.Project;
import com.example.edecision.repository.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    public ProjectRepository repo;

    public List<Project> getAll(){
        return repo.findAll();
    }
}
