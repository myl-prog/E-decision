package com.example.edecision.repository.proposition;

import com.example.edecision.model.vote.PropositionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropositionStatusRepository extends JpaRepository<PropositionStatus, Integer> {
}
