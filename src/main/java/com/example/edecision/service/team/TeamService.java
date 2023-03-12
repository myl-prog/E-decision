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

    /**
     * Get all teams
     */
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Get a team by id
     *
     * @param id team id that we want to get
     * @return a team
     */
    public Team getTeamById(int id) {
        return teamRepository.findById(id).orElseThrow(() -> new CustomException("No team found with this id : " + id, HttpStatus.NOT_FOUND));
    }

    /**
     * Permit to create a team and create all userTeam provided
     *
     * @param teamBody teamBody object
     * @return the created team
     */
    public Team createTeam(TeamBody teamBody) {
        if (teamRepository.findByName(teamBody.getTeam().getName()).isPresent()) {
            throw new CustomException("A team with this name already exists", HttpStatus.CONFLICT);
        }
        User owner = Common.GetCurrentUser();
        teamBody.getTeam().setOwner(owner);
        List<User> usersToAddInTeam = userRepository.getUsersById(teamBody.getUserIdList());
        if (usersToAddInTeam.size() < teamBody.getUserIdList().size()) {
            throw new CustomException("One user or more that you have provided don't exist", HttpStatus.NOT_FOUND);
        }
        Team createdTeam = teamRepository.save(teamBody.getTeam());
        teamBody.getUserIdList().forEach((userId) -> {
            UserTeam userTeam = new UserTeam(userId, createdTeam.getId());
            userTeamRepository.save(userTeam);
        });
        return createdTeam;
    }

    /**
     * Permit to update an existing team and modify userTeam
     *
     * @param teamId   teamId that we want to update
     * @param teamBody teamBody object
     * @return the updated team
     */
    public Team updateTeam(int teamId, TeamBody teamBody) {
        if (teamRepository.findById(teamId).isPresent()) {
            Team updatedTeam = teamRepository.findById(teamId).get();
            verifyUserOwnership(updatedTeam);
            if (teamRepository.findByName(teamBody.getTeam().getName()).isPresent()) {
                Team foundTeam = teamRepository.findByName(teamBody.getTeam().getName()).get();
                if (foundTeam.getId() != teamId) {
                    throw new CustomException("A team with this name already exists", HttpStatus.CONFLICT);
                }
            }
            List<User> oldUserList = userRepository.findAllUsersByTeamId(teamId);

            modifyUsersInTeam(teamId, teamBody, oldUserList);

            updatedTeam.setName(teamBody.getTeam().getName());
            updatedTeam.setTeamTypeId(teamBody.getTeam().getTeamTypeId());
            updatedTeam.setOwner(Common.GetCurrentUser());
            return teamRepository.save(updatedTeam);
        } else {
            throw new CustomException("No team found with this id : " + teamId, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Permit to delete an existing team, delete all userTeam associated
     *
     * @param id teamId that we want to delete
     */
    public void deleteTeam(int id) {
        if (teamRepository.findById(id).isPresent()) {
            Team deletedTeam = teamRepository.findById(id).get();
            verifyUserOwnership(deletedTeam);
            userTeamRepository.deleteAllUserTeam(id);
            teamRepository.deleteById(id);
        } else {
            throw new CustomException("No team found with this id : " + id, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all teams where project_id is null
     * and add his associated users
     *
     * @param teamsIds teamsId
     * @return a team list
     */
    public List<Team> getFreeTeams(List<Integer> teamsIds) {
        List<Team> teamList = teamRepository.getFreeTeamsForProjectCreation(teamsIds);
        return getTeamsWithUsers(teamList);
    }

    /**
     * Get all teams associated to a project
     *
     * @param projectId projectId
     * @return a team list
     */
    public List<Team> getTeamsByProject(int projectId) {
        List<Team> teamList = teamRepository.getTeamsByProject(projectId);
        return getTeamsWithUsers(teamList);
    }

    public List<Team> getTeamsByProposition(int propositionId) {
        List<Team> teamList = teamRepository.getTeamsByProposition(propositionId);
        return getTeamsWithUsers(teamList);
    }

    /**
     * For each team in teamList, add associated users
     *
     * @param teamList teamList
     * @return a team list
     */
    private List<Team> getTeamsWithUsers(List<Team> teamList) {
        teamList.forEach(team -> team.setUsers(userRepository.findAllUsersByTeamId(team.getId())));
        return teamList;
    }

    /**
     * Modify team's associated users during update
     *
     * @param teamId      teamId
     * @param teamBody    teamBody
     * @param oldUserList oldUserList
     */
    private void modifyUsersInTeam(int teamId, TeamBody teamBody, List<User> oldUserList) {
        teamBody.getUserIdList().forEach(userId -> {
            if (oldUserList.stream().noneMatch(user -> user.getId() == userId)) {
                userTeamRepository.save(new UserTeam(userId, teamId));
            }
        });

        oldUserList.forEach(user -> {
            if (teamBody.getUserIdList().stream().noneMatch(userId -> userId == user.getId())) {
                userTeamRepository.deleteUserTeam(user.getId(), teamId);
            }
        });
    }

    /**
     * Verify if current user is the team owner
     *
     * @param team team
     */
    private void verifyUserOwnership(Team team) {
        if (Common.GetCurrentUser().getId() != team.getOwner().getId()) {
            throw new CustomException("You are not the team owner, you can't perform this action", HttpStatus.UNAUTHORIZED);
        }
    }
}
