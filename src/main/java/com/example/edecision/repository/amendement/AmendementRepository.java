package com.example.edecision.repository.amendement;

import com.example.edecision.model.amendement.Amendement;
import com.example.edecision.model.proposition.Proposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AmendementRepository extends JpaRepository<Amendement, Integer> {

    @Query(
            value = "SELECT * FROM amendement " +
                    "INNER JOIN proposition ON proposition.id = amendement.amend_proposition_id " +
                    "WHERE amendement.id = :amendementId " +
                    "AND proposition.id = :propositionId " +
                    "AND project_id = :projectId ",
            nativeQuery = true)
    Optional<Amendement> getProjectPropositionAmendementById(@Param("projectId") int projectId, @Param("propositionId") int propositionId, @Param("amendementId") int amendementId);

    @Query(
            value = "SELECT * FROM amendement " +
                    "INNER JOIN proposition ON proposition.id = amendement.amend_proposition_id " +
                    "WHERE proposition.id = :propositionId " +
                    "AND project_id = :projectId ",
            nativeQuery = true)
    List<Amendement> getProjectPropositionAmendements(@Param("projectId") int projectId, @Param("propositionId") int propositionId);

    @Transactional
    @Modifying
    @Query("DELETE from amendement where id = :amendementId")
    void deleteAmendementById(@Param("amendementId") int amendementId);
}
