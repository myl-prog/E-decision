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

    @Query(value = "select team.* from team_proposition,team where proposition_id=:proposition_id and team_id = team.id", nativeQuery = true)
    ArrayList<Team> getTeamsByProposition(@Param("proposition_id") Integer proposition_id);

    @Query(value = "SELECT * FROM team WHERE project_id = :projectId", nativeQuery = true)
    List<Team> getTeamsByProject(@Param("projectId") int projectId);

    @Query(value = "SELECT team.* FROM team WHERE team.project_id is NULL AND id in :ids", nativeQuery = true)
    ArrayList<Team> getFreeTeamsForProjectCreation(@Param("ids") List<Integer> ids);

    @Transactional
    @Modifying
    @Query(value = "UPDATE team SET project_id = NULL WHERE project_id=:projectId", nativeQuery = true)
    void removeProjectIdFromTeams(@Param("projectId") int projectId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE team SET project_id = NULL WHERE project_id = :projectId AND id = :teamId", nativeQuery = true)
    void removeProjectIdFromTeam(@Param("projectId") int projectId, @Param("teamId") int teamId);
}
