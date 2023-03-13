package com.example.edecision.repository.team;

import com.example.edecision.model.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Integer> {

    Optional<Team> findByName(String name);

    @Query(value = "SELECT * FROM team WHERE project_id = :projectId",
            nativeQuery = true)
    List<Team> getTeamsByProject(@Param("projectId") int projectId);

    @Query(
            value = "SELECT * FROM team " +
                    "INNER JOIN team_proposition ON team.id = team_proposition.team_id " +
                    "WHERE team_proposition.proposition_id = :propositionId",
            nativeQuery = true)
    List<Team> getTeamsByProposition(@Param("propositionId") int propositionId);

    @Query(value = "SELECT team.* FROM team WHERE team.project_id is NULL AND id in :ids",
            nativeQuery = true)
    ArrayList<Team> getFreeTeamsForProjectCreation(@Param("ids") List<Integer> ids);

    @Transactional
    @Modifying
    @Query(value = "UPDATE team SET project_id = NULL WHERE project_id=:projectId",
            nativeQuery = true)
    void removeProjectIdFromTeams(@Param("projectId") int projectId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE team SET project_id = NULL WHERE project_id = :projectId AND id = :teamId",
            nativeQuery = true)
    void removeProjectIdFromTeam(@Param("projectId") int projectId, @Param("teamId") int teamId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE team SET project_id = :projectId WHERE id IN :ids AND project_id IS NULL",
            nativeQuery = true)
    void addProjectToTeams(@Param("ids") List<Integer> ids, @Param("projectId") int projectId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE team SET project_id = :projectId WHERE id = :teamId AND project_id IS NULL",
            nativeQuery = true)
    void addProjectToTeam(@Param("teamId") int teamId, @Param("projectId") int projectId);
}
