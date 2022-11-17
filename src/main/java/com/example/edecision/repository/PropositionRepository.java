package com.example.edecision.repository;

import com.example.edecision.model.Proposition;
import com.example.edecision.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropositionRepository extends JpaRepository<Proposition, Integer> {
}
