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
    @Query(value = "DELETE FROM user_team WHERE user_id = :userId AND team_id = :teamId",
            nativeQuery = true)
    void deleteUserTeam(@Param("userId") int userId, @Param("teamId") int teamId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_team WHERE team_id = :teamId",
            nativeQuery = true)
    void deleteAllUserTeam(@Param("teamId") int teamId);
}
