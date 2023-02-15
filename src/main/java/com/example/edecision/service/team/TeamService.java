package com.example.edecision.service.team;

import com.example.edecision.model.team.Team;
import com.example.edecision.repository.team.TeamRepository;
import com.example.edecision.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {
    @Autowired
    public TeamRepository teamRepository;

    @Autowired
    public UserRepository userRepository;

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeam(int id) {
        return teamRepository.findById(id).get();
    }

    public Team createTeam(Team Team) {
        return teamRepository.save(Team);
    }

    public void deleteTeam(int id) {
        teamRepository.deleteById(id);
    }

    public List<Team> getFreeTeamsWithUsers(int[] teamsIds) {
        List<Team> teams = teamRepository.getFreeTeams(teamsIds);
        for (Team team : teams) {
            team.setUsers(userRepository.getUsersByTeam(team.getId()));
        }
        return teams;
    }
}
