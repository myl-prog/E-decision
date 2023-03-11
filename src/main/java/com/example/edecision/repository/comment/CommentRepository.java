package com.example.edecision.repository.comment;

import com.example.edecision.model.amendement.Amendement;
import com.example.edecision.model.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query(value = "SELECT * FROM comment " +
                    "INNER JOIN proposition ON proposition.id = comment.proposition_id " +
                    "WHERE proposition.id = :propositionId " +
                    "AND project_id = :projectId " +
                    "AND is_deleted = 1",
            nativeQuery = true)
    Optional<Comment> getPropositionDeletedComment(@Param("projectId") int projectId, @Param("propositionId") int propositionId);

    @Query(value = "SELECT * FROM comment " +
                    "INNER JOIN proposition ON proposition.id = comment.proposition_id " +
                    "WHERE proposition.id = :propositionId " +
                    "AND project_id = :projectId " +
                    "AND is_escalated = 1",
            nativeQuery = true)
    Optional<Comment> getPropositionEscalateComment(@Param("projectId") int projectId, @Param("propositionId") int propositionId);

    @Transactional
    @Modifying
    @Query( value = "DELETE FROM comment " +
                    "WHERE proposition_id = :propositionId",
            nativeQuery = true)
    void deleteCommentsByProposition(@Param("propositionId") int propositionId);
}
