package com.example.edecision.repository.team;

import com.example.edecision.model.proposition.PropositionStatus;
import com.example.edecision.model.team.TeamType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamTypeRepository extends JpaRepository<TeamType, Integer> {
}
