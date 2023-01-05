package com.example.edecision.repository;

import com.example.edecision.model.Team;
import com.example.edecision.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {

}
