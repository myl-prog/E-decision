package com.example.edecision.repository.proposition;

import com.example.edecision.model.proposition.Proposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PropositionRepository extends JpaRepository<Proposition, Integer> {
    @Transactional
    @Modifying
    @Query("delete from proposition where id=:proposition_id")
    void deleteProposition(@Param("proposition_id") Integer proposition_id);

    @Query(value = "select * from proposition where id in (select proposition_id from team_proposition where team_id in (select team_id from user_team where user_id =:user_id))", nativeQuery = true)
    List<Proposition> getPropositionsByUser(@Param("user_id") Integer user_id);

    @Query(value = "select * from proposition where id=:proposition_id and id in (select proposition_id from team_proposition where team_id in (select team_id from user_team where user_id =:user_id))", nativeQuery = true)
    Proposition getPropositionByUser(@Param("proposition_id") Integer proposition_id, @Param("user_id") Integer user_id);
}
