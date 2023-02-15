package com.example.edecision.repository.project;

import com.example.edecision.model.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE team SET project_id =:project_id WHERE id IN :ids AND project_id IS NULL", nativeQuery = true)
    void addProjectToTeam(@Param("ids") int[] ids, @Param("project_id") Integer project_id);

}
