package com.example.edecision.service.team;

import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.team.Team;
import com.example.edecision.model.team.TeamBody;
import com.example.edecision.model.team.UserTeam;
import com.example.edecision.model.user.User;
import com.example.edecision.repository.team.TeamRepository;
import com.example.edecision.repository.team.UserTeamRepository;
import com.example.edecision.repository.user.UserRepository;
import com.example.edecision.utils.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {
    @Autowired
    public TeamRepository teamRepository;

    @Autowired
    public UserTeamRepository userTeamRepository;

    @Autowired
    public UserRepository userRepository;

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeamById(int id) {
        return teamRepository.findById(id).orElseThrow(() -> new CustomException("No team found with this id : " + id, HttpStatus.NOT_FOUND));
    }

    public Team createTeam(TeamBody teamBody) {
        // Todo !! Is the team owner should mandatory be part of the team he has created ?
        if (teamRepository.findByName(teamBody.team.getName()).isPresent()) {
            throw new CustomException("A team with this name already exists", HttpStatus.CONFLICT);
        }
        User owner = Common.GetCurrentUser();
        teamBody.team.setOwner(owner);
        teamBody.userIdList.forEach(userId -> {
            if (userRepository.findById(userId).isEmpty()) {
                throw new CustomException("User with id : " + userId + " does not exists", HttpStatus.BAD_REQUEST);
            }
        });
        teamBody.userIdList.forEach((userId) -> {
            UserTeam userTeam = new UserTeam(userId, teamBody.team.getId());
            userTeamRepository.save(userTeam);
        });
        return teamRepository.save(teamBody.team);
    }

    public Team updateTeam(int teamId, TeamBody teamBody) {
        if (teamRepository.findById(teamId).isPresent()) {
            Team updatedTeam = teamRepository.findById(teamId).get();
            verifyUserOwnership(teamBody.team);
            if (teamRepository.findByName(teamBody.team.getName()).isPresent()) {
                throw new CustomException("A team with this name already exists", HttpStatus.CONFLICT);
            }
            List<User> oldUserList = userTeamRepository.findAllUsersByTeamId(teamId);

            teamBody.userIdList.forEach(userId -> {
                if (!oldUserList.stream().anyMatch(user -> user.getId() == userId)) {
                    userTeamRepository.save(new UserTeam(userId, teamId));
                }
            });

            teamBody.userIdList.forEach(userId -> {
                if (oldUserList.stream().anyMatch(user -> user.getId() == userId)) {
                    userTeamRepository.deleteUserTeam(userId, teamId);
                }
            });

            updatedTeam.setName(teamBody.team.getName());
            updatedTeam.setTeam_type_id(teamBody.team.getTeam_type_id());
            return updatedTeam;
        }
        throw new CustomException("No team found with this id : " + teamBody.team.getId(), HttpStatus.NOT_FOUND);
    }

    public void deleteTeam(int id) {
        if (teamRepository.findById(id).isPresent()) {
            Team deletedTeam = teamRepository.findById(id).get();
            verifyUserOwnership(deletedTeam);
            teamRepository.deleteById(id);
        }
        throw new CustomException("No team found with this id : " + id, HttpStatus.NOT_FOUND);
    }

    public List<Team> getFreeTeamsWithUsers(int[] teamsIds) {
        List<Team> teams = teamRepository.getFreeTeamsForProjectCreation(teamsIds);
        for (Team team : teams) {
            team.setUsers(userTeamRepository.findAllUsersByTeamId(team.getId()));
        }
        return teams;
    }

    private void verifyUserOwnership(Team team) {
        if (Common.GetCurrentUser().getId() != team.getOwner().getId()) {
            throw new CustomException("You are not the team owner, you can't perform this action", HttpStatus.UNAUTHORIZED);
        }
    }
}
