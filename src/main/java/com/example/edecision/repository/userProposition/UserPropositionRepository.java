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
    @Query(value = "INSERT INTO user_proposition (user_id, proposition_id) VALUES (:user_id, :proposition_id)",
            nativeQuery = true)
    void createUserProposition(@Param("proposition_id") int proposition_id, @Param("user_id") int user_id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_proposition WHERE proposition_id=:proposition_id",
            nativeQuery = true)
    void deleteUserPropositionsByProposition(@Param("proposition_id") int proposition_id);

    @Transactional
    @Modifying
    @Query(value = "DELETe FROM user_proposition WHERE proposition_id=:proposition_id AND user_id=:user_id",
            nativeQuery = true)
    void deleteUserProposition(@Param("proposition_id") int proposition_id, @Param("user_id") int user_id);
}
