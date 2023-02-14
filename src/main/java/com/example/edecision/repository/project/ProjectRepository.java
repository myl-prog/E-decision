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
    @Query(value = "INSERT INTO team (project_id) VALUES (:project_id)", nativeQuery = true)
    void addProjectToTeam(@Param("project_id") Integer projectId);

}
