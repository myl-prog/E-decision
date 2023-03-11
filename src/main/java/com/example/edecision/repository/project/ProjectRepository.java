package com.example.edecision.repository.project;

import com.example.edecision.model.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

    // Todo, move these 2 methods to team repository, its make more sense
    @Transactional
    @Modifying
    @Query(value = "UPDATE team SET project_id =:project_id WHERE id IN :ids AND project_id IS NULL",
            nativeQuery = true)
    void addProjectToTeams(@Param("ids") List<Integer> ids, @Param("project_id") Integer project_id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE team SET project_id = :project_id WHERE id = :teamId AND project_id IS NULL",
            nativeQuery = true)
    void addProjectToTeam(@Param("teamId") int teamId, @Param("project_id") Integer project_id);

}
