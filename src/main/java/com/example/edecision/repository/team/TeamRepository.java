package com.example.edecision.repository.team;

import com.example.edecision.model.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

public interface TeamRepository extends JpaRepository<Team, Integer> {

    @Query(value = "select team.* from team_proposition,team where proposition_id=:proposition_id and team_id = team.id", nativeQuery = true)
    ArrayList<Team> getTeamsByProposition(@Param("proposition_id") Integer proposition_id);

    @Query(value = "SELECT team.* FROM team WHERE team.project_id is NULL AND id in :ids", nativeQuery = true)
    ArrayList<Team> getFreeTeams(int[] ids);

    @Transactional
    @Modifying
    @Query(value = "UPDATE team SET project_id = NULL WHERE project_id=:projectId", nativeQuery = true)
    void removeProjectIdFromTeams(@Param("projectId") int projectId);
}
