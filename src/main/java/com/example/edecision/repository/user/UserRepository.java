package com.example.edecision.repository.user;

import com.example.edecision.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLogin(String login);

    @Query(value = "SELECT * FROM user WHERE id in :ids",
            nativeQuery = true)
    List<User> getUsersById(@Param("ids") List<Integer> userIdList);

    @Query(
            value = "SELECT user.* FROM user_proposition,user " +
                    "WHERE proposition_id = :propositionId " +
                    "AND user_id = user.id",
            nativeQuery = true)
    List<User> getUsersByProposition(@Param("propositionId") int propositionId);

    @Query(
            value = "SELECT user.* FROM user " +
                    "INNER JOIN user_team ON user.id = user_team.user_id " +
                    "WHERE user_team.team_id = :teamId",
            nativeQuery = true)
    List<User> findAllUsersByTeamId(@Param("teamId") int teamId);

    @Query(
            value = "SELECT * FROM user " +
                    "INNER JOIN project_user ON user.id = project_user.user_id " +
                    "WHERE project_user.is_own = true " +
                    "AND project_user.project_id = :projectId",
            nativeQuery = true)
    User getProjectOwner(@Param("projectId") int projectId);

}
