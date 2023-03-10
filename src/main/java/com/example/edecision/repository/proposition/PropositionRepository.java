package com.example.edecision.repository.proposition;

import com.example.edecision.model.proposition.Proposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PropositionRepository extends JpaRepository<Proposition, Integer> {
    @Transactional
    @Modifying
    @Query("delete from proposition where id=:proposition_id")
    void deleteProposition(@Param("proposition_id") Integer proposition_id);

    @Query(value = "select * from proposition where id in (select proposition_id from team_proposition where team_id in (select team_id from user_team where user_id =:user_id))", nativeQuery = true)
    List<Proposition> getPropositionsByUser(@Param("user_id") Integer user_id);

    @Query(
            value = "SELECT * FROM proposition " +
                    "WHERE id = :propositionId " +
                    "AND project_id = :projectId ",
            nativeQuery = true)
    Optional<Proposition> getProjectPropositionById(@Param("projectId") int projectId, @Param("propositionId") int propositionId);

    @Query(
            value = "SELECT * FROM proposition " +
                    "WHERE :userId IN (SELECT user_id FROM user_proposition WHERE proposition_id = proposition.id) " +
                    "OR :userId IN (SELECT user_id FROM user_team WHERE user_team.team_id IN (SELECT team_id from team_proposition WHERE proposition_id = proposition.id)) " +
                    "ORDER BY begin_time DESC LIMIT 1",
            nativeQuery = true)
    Optional<Proposition> getLastPropositionByUserId(@Param("userId") int userId);
}
