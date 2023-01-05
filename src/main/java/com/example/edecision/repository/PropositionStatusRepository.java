package com.example.edecision.repository;

import com.example.edecision.model.Proposition;
import com.example.edecision.model.PropositionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropositionStatusRepository extends JpaRepository<PropositionStatus, Integer> {
}
