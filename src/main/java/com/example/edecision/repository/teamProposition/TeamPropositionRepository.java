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
    @Query(value = "INSERT INTO team_proposition (team_id, proposition_id) VALUES (:teamId, :propositionId)",
            nativeQuery = true)
    void createTeamProposition(@Param("propositionId") int propositionId, @Param("teamId") int teamId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM team_proposition WHERE proposition_id = :propositionId",
            nativeQuery = true)
    void deleteTeamPropositionsByProposition(@Param("propositionId") int propositionId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM team_proposition WHERE proposition_id = :propositionId AND team_id = :teamId",
            nativeQuery = true)
    void deleteTeamProposition(@Param("propositionId") int propositionId, @Param("teamId") int teamId);
}
