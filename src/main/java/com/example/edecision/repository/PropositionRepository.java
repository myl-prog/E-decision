package com.example.edecision.repository;

import com.example.edecision.model.Proposition;
import com.example.edecision.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PropositionRepository extends JpaRepository<Proposition, Integer> {
    @Transactional
    @Modifying
    @Query("delete from proposition where id=:proposition_id")
    void deleteProposition(@Param("proposition_id") Integer proposition_id);
}
