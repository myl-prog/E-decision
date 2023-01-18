package com.example.edecision.repository.user;

import com.example.edecision.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLogin(String login);

    @Query(value = "select user.* from user_proposition,user where proposition_id=:proposition_id and user_id = user.id", nativeQuery = true)
    ArrayList<User> getUsersByProposition(@Param("proposition_id") Integer proposition_id);
}
