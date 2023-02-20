package com.example.edecision.repository.team;

import com.example.edecision.model.team.UserTeam;
import com.example.edecision.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserTeamRepository extends JpaRepository<UserTeam, Integer> {

    @Query(value = "SELECT user_team.user_id FROM user_team WHERE team_id = :teamId", nativeQuery = true)
    List<User> findAllUsersByTeamId(@Param("teamId") int teamId);

    @Transactional
    @Modifying
    @Query("delete from user_team where user_id = :userId and team_id = :teamId")
    void deleteUserTeam(@Param("userId") Integer userId, @Param("teamId") Integer teamId);
}
