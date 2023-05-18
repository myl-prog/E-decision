package com.example.edecision.repository.vote;

import com.example.edecision.model.vote.CommentVote;
import com.example.edecision.model.vote.PropositionVote;
import com.example.edecision.model.vote.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PropositionVoteRepository extends JpaRepository<PropositionVote, Integer> {

    @Query( value = "SELECT proposition_vote.* FROM proposition_vote " +
                    "INNER JOIN proposition ON proposition_id = proposition.id " +
                    "INNER JOIN user ON user_id = user.id " +
                    "INNER JOIN vote_type ON vote_type_id = vote_type.id " +
                    "WHERE proposition_id = :propositionId " +
                    "AND project_id = :projectId",
            nativeQuery = true)
    List<PropositionVote> getProjectPropositionVotesById(@Param("projectId") int projectId, @Param("propositionId") int propositionId);

    @Query( value = "SELECT * FROM proposition_vote " +
                    "INNER JOIN proposition ON proposition_id = proposition.id " +
                    "INNER JOIN amendement ON proposition.id = amendement.amend_proposition_id " +
                    "WHERE proposition_id = :propositionId " +
                    "AND project_id = :projectId " +
                    "AND amendement_id = :amendementId",
            nativeQuery = true)
    List<PropositionVote> getProjectPropositionAmendementVotesById(@Param("projectId") int projectId, @Param("propositionId") int propositionId, @Param("amendementId") int amendementId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO proposition_vote (user_id, proposition_id,vote_type_id) VALUES (:userId, :propositionId, :voteTypeId)",
            nativeQuery = true)
    void createProjectPropositionVote(@Param("userId") int userId, @Param("propositionId") int propositionId, @Param("voteTypeId") int voteTypeId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO proposition_vote (user_id, proposition_id, amendement_id, vote_type_id) VALUES (:userId, :propositionId, :amendementId, :voteTypeId)",
            nativeQuery = true)
    void createProjectPropositionAmendementVote(@Param("userId") int userId, @Param("propositionId") int propositionId, @Param("amendementId") int amendementId, @Param("voteTypeId") int voteTypeId);

    @Transactional
    @Modifying
    @Query( value = "DELETE FROM proposition_vote " +
                    "WHERE proposition_id = :propositionId",
            nativeQuery = true)
    void deleteProjectPropositionVotesById(@Param("propositionId") int propositionId);
}
