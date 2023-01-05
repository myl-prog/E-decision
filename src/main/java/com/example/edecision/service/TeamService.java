package com.example.edecision.service;

import com.example.edecision.model.Team;
import com.example.edecision.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {
    @Autowired
    public TeamRepository TeamRepository;

    public List<Team> getAllTeams() {
        return TeamRepository.findAll();
    }

    public Team getTeam(int id) {
        return TeamRepository.findById(id).get();
    }

    public Team createTeam(Team Team) {
        return TeamRepository.save(Team);
    }

    public void deleteTeam(int id) {
        TeamRepository.deleteById(id);
    }
}
