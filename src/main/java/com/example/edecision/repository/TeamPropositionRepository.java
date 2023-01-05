package com.example.edecision.repository;

import com.example.edecision.model.Proposition;
import com.example.edecision.model.TeamProposition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamPropositionRepository extends JpaRepository<TeamProposition, Integer> {
}
