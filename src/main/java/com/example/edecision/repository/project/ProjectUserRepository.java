package com.example.edecision.repository.project;

import com.example.edecision.model.project.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Integer> {

    //@Transactional
    //@Modifying
    //@Query(value = "", nativeQuery = true)
    //void addUsersToProject();
}