package com.example.edecision.repository.vote;

import com.example.edecision.model.amendement.Amendement;
import com.example.edecision.model.vote.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteTypeRepository extends JpaRepository<VoteType, Integer> {
}
