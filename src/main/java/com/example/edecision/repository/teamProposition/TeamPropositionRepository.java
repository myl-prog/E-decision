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
    @Query(value = "INSERT INTO team_proposition (team_id, proposition_id) VALUES (:team_id, :proposition_id)",
            nativeQuery = true)
    void createTeamProposition(@Param("proposition_id") Integer proposition_id, @Param("team_id") Integer team_id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM team_proposition WHERE proposition_id=:proposition_id",
            nativeQuery = true)
    void deleteTeamPropositionsByProposition(@Param("proposition_id") Integer proposition_id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM team_proposition WHERE proposition_id=:proposition_id AND team_id=:team_id",
            nativeQuery = true)
    void deleteTeamProposition(@Param("proposition_id") Integer proposition_id, @Param("team_id") Integer team_id);
}
