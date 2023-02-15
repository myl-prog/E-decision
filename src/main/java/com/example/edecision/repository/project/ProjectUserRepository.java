package com.example.edecision.repository.project;

import com.example.edecision.model.project.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Integer> {
}