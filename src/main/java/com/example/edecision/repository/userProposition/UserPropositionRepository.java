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
    @Query(value = "insert into user_proposition (user_id, proposition_id) values (:user_id, :proposition_id)", nativeQuery = true)
    void createUserProposition(@Param("proposition_id") Integer proposition_id, @Param("user_id") Integer user_id);

    @Transactional
    @Modifying
    @Query("delete from user_proposition where proposition_id=:proposition_id")
    void deleteUserPropositionsByProposition(@Param("proposition_id") Integer proposition_id);

    @Transactional
    @Modifying
    @Query("delete from user_proposition where proposition_id=:proposition_id and user_id=:user_id")
    void deleteUserProposition(@Param("proposition_id") Integer proposition_id, @Param("user_id") Integer user_id);
}
