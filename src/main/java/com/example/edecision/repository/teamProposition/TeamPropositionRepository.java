package com.example.edecision.repository.teamProposition;

import com.example.edecision.model.teamProposition.TeamProposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TeamPropositionRepository extends JpaRepository<TeamProposition, Integer> {

    @Transactional
    @Modifying
    @Query(value = "insert into team_proposition (team_id, proposition_id) values (:team_id, :proposition_id)", nativeQuery = true)
    void createTeamProposition(@Param("proposition_id") Integer proposition_id, @Param("team_id") Integer team_id);

    @Transactional
    @Modifying
    @Query("delete from team_proposition where proposition_id=:proposition_id")
    void deleteTeamPropositionsByProposition(@Param("proposition_id") Integer proposition_id);

    @Transactional
    @Modifying
    @Query("delete from team_proposition where proposition_id=:proposition_id and team_id=:team_id")
    void deleteTeamProposition(@Param("proposition_id") Integer proposition_id, @Param("team_id") Integer team_id);
}
