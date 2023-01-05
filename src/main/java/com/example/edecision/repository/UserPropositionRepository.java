package com.example.edecision.repository;

import com.example.edecision.model.Proposition;
import com.example.edecision.model.UserProposition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPropositionRepository extends JpaRepository<UserProposition, Integer> {
}
