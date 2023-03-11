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
    @Query(value = "DELETE FROM proposition WHERE id=:proposition_id",
            nativeQuery = true)
    void deleteProposition(@Param("proposition_id") Integer proposition_id);

    @Query(value = "SELECT * FROM proposition " +
                    "WHERE id IN (SELECT proposition_id FROM team_proposition WHERE team_id IN (SELECT team_id FROM user_team WHERE user_id =:userId))" +
                    "OR id IN (SELECT proposition_id FROM user_proposition WHERE user_id = :userId)",
            nativeQuery = true)
    List<Proposition> getPropositionsByUser(@Param("userId") Integer userId);

    @Query(value = "SELECT * FROM proposition " +
                    "WHERE id = :propositionId " +
                    "AND project_id = :projectId ",
            nativeQuery = true)
    Optional<Proposition> getProjectPropositionById(@Param("projectId") int projectId, @Param("propositionId") int propositionId);

    @Query(value = "SELECT * FROM proposition " +
                    "WHERE :userId IN (SELECT user_id FROM user_proposition WHERE proposition_id = proposition.id) " +
                    "OR :userId IN (SELECT user_id FROM user_team WHERE user_team.team_id IN (SELECT team_id from team_proposition WHERE proposition_id = proposition.id)) " +
                    "ORDER BY begin_time DESC LIMIT 1",
            nativeQuery = true)
    Optional<Proposition> getLastPropositionByUserId(@Param("userId") int userId);
}
