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

    @Query( value = "SELECT * FROM proposition_vote " +
            "INNER JOIN proposition ON proposition_id = id " +
            "WHERE proposition_id = :propositionId " +
            "AND project_id = :projectId",
            nativeQuery = true)
    List<PropositionVote> getProjectPropositionVotesById(@Param("projectId") int projectId, @Param("propositionId") int propositionId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO proposition_vote (user_id, proposition_id,vote_type_id) VALUES (:userId, :propositionId, :voteTypeId)",
            nativeQuery = true)
    void createProjectPropositionVote(@Param("userId") int userId, @Param("propositionId") int propositionId, @Param("voteTypeId") int voteTypeId);

    @Transactional
    @Modifying
    @Query( value = "DELETE FROM propositon_vote " +
                    "WHERE proposition_id = :propositionId",
            nativeQuery = true)
    void deleteProjectPropositionVotesById(@Param("propositionId") int propositionId);
}
