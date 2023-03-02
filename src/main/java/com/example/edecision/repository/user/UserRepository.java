package com.example.edecision.repository.user;

import com.example.edecision.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLogin(String login);

    @Query(value = "SELECT * FROM user WHERE id in :ids", nativeQuery = true)
    List<User> getUsersById(@Param("ids") List<Integer> userIdList);

    @Query(value = "select user.* from user_proposition,user where proposition_id=:proposition_id and user_id = user.id", nativeQuery = true)
    List<User> getUsersByProposition(@Param("proposition_id") Integer proposition_id);

    @Query(value = "SELECT user.* FROM user INNER JOIN user_team ON user.id = user_team.user_id WHERE user_team.team_id = :teamId", nativeQuery = true)
    List<User> findAllUsersByTeamId(@Param("teamId") Integer teamId);

    @Query(value = "SELECT * FROM user INNER JOIN project_user ON user.id = project_user.user_id WHERE project_user.project_id = :projectId", nativeQuery = true)
    List<User> getUsersByProject(@Param("projectId") int projectId);

    @Query(value = "SELECT * FROM user INNER JOIN project_user ON user.id = project_user.user_id WHERE project_user.is_own = true AND project_user.project_id = :projectId", nativeQuery = true)
    User getProjectOwner(@Param("projectId") int projectId);

    @Query(
            value = "SELECT * FROM user " +
                    "INNER JOIN user_proposition ON user.id = user_proposition.user_id " +
                    "WHERE user_proposition.proposition_id = :propositionId",
            nativeQuery = true
    )
    User getPropositionOwner(@Param("propositionId") int propositionId);
}
