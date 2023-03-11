package com.example.edecision.repository.vote;

import com.example.edecision.model.amendement.Amendement;
import com.example.edecision.model.vote.CommentVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentVoteRepository extends JpaRepository<CommentVote, Integer> {
    @Query( value = "SELECT * FROM comment_vote " +
                    "WHERE comment_id = :commentId",
            nativeQuery = true)
    List<CommentVote> getCommentVotes(@Param("commentId") int commentId);

    @Transactional
    @Modifying
    @Query( value = "DELETE FROM comment_vote " +
                    "WHERE comment_id IN (SELECT id FROM comment WHERE proposition_id = :propositionId)",
            nativeQuery = true)
    void deleteCommentVotesByProposition(@Param("propositionId") int propositionId);
}
