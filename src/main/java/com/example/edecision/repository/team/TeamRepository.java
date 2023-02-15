package com.example.edecision.repository.team;

import com.example.edecision.repository.teamProposition.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Integer> {

    @Query(value = "select team.* from team_proposition,team where proposition_id=:proposition_id and team_id = team.id", nativeQuery = true)
    ArrayList<Team> getTeamsByProposition(@Param("proposition_id") Integer proposition_id);

    @Query(value = "SELECT team.* FROM TEAM WHERE team.project_id is NULL AND id in :ids", nativeQuery = true)
    ArrayList<Team> getFreeTeams(int[] ids);
}
