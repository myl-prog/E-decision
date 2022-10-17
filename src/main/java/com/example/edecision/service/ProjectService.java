package com.example.edecision.service;
import com.example.edecision.model.Project;
import com.example.edecision.repository.ProjectRepository;
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
