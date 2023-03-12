package com.example.edecision.repository.userProposition;

import com.example.edecision.model.userProposition.UserProposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserPropositionRepository extends JpaRepository<UserProposition, Integer> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO user_proposition (user_id, proposition_id) VALUES (:userId, :propositionId)",
            nativeQuery = true)
    void createUserProposition(@Param("propositionId") int propositionId, @Param("userId") int userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_proposition WHERE proposition_id = :propositionId",
            nativeQuery = true)
    void deleteUserPropositionsByProposition(@Param("propositionId") int propositionId);

    @Transactional
    @Modifying
    @Query(value = "DELETe FROM user_proposition WHERE proposition_id = :propositionId AND user_id = :userId",
            nativeQuery = true)
    void deleteUserProposition(@Param("propositionId") int propositionId, @Param("userId") int userId);
}
