package com.example.edecision.repository.team;

import com.example.edecision.model.team.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserTeamRepository extends JpaRepository<UserTeam, Integer> {

    @Transactional
    @Modifying
    @Query("delete from user_team where user_id = :userId and team_id = :teamId")
    void deleteUserTeam(@Param("userId") Integer userId, @Param("teamId") Integer teamId);

    @Transactional
    @Modifying
    @Query("delete from user_team where team_id = :teamId")
    void deleteAllUserTeam(@Param("teamId") Integer teamId);
}
