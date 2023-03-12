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

    // ============
    // === Team ===
    // ============

    /**
     * Récupère toutes les équipes
     * @return la liste des équipes
     */
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Récupère une équipe avec son identifiant
     *
     * @param id id de l'équipe
     * @return l'équipe
     */
    public Team getTeamById(int id) {
        return teamRepository.findById(id).orElseThrow(() -> new CustomException("No team found with this id : " + id, HttpStatus.NOT_FOUND));
    }

    /**
     * Permet de créer une équipe en associant tous ses utilisateurs
     *
     * @param teamBody objet de l'équipe et l'association de ses utilisateurs
     * @return l'équipe créée
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
     * Permet de mettre à jour une équipe
     *
     * @param teamId   id de l'équipe
     * @param teamBody objet de l'équipe
     * @return l'équipe mise à jour
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
     * Permet de supprimer une équipe et ses associations avec ses utilisateurs
     *
     * @param id id de la team
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
     * Permet de récupérer toutes les équipes qui ne sont pas rattachées à un projet
     *
     * @param teamsIds id des teams
     * @return la liste des équipes libres avec leurs utilisateurs
     */
    public List<Team> getFreeTeams(List<Integer> teamsIds) {
        List<Team> teamList = teamRepository.getFreeTeamsForProjectCreation(teamsIds);
        return getTeamsWithUsers(teamList);
    }

    /**
     * Permet toutes les équipes d'une proposition
     *
     * @param propositionId id de la proposition
     * @return la liste des équipes rattachées à la proposition avec leurs utilisateurs
     */
    public List<Team> getTeamsByProposition(int propositionId) {
        List<Team> teamList = teamRepository.getTeamsByProposition(propositionId);
        return getTeamsWithUsers(teamList);
    }

    /**
     * Permet de retourner les équipes avec les utilisateurs associés
     *
     * @param teamList liste d'équipes
     * @return liste d'équipes avec utilisateurs associés
     */
    private List<Team> getTeamsWithUsers(List<Team> teamList) {
        teamList.forEach(team -> team.setUsers(userRepository.findAllUsersByTeamId(team.getId())));
        return teamList;
    }

    /**
     * Met à jour les utilisateurs associés à l'équipe pendant la modification
     *
     * @param teamId      id de l'équipe
     * @param teamBody    objet de l'équipe
     * @param oldUserList ancienne liste d'utilisateurs associés à l'équipe
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
     * Vérifie si l'utilisateur courant est le gestionnaire de l'équipe
     *
     * @param team équipe
     */
    private void verifyUserOwnership(Team team) {
        if (Common.GetCurrentUser().getId() != team.getOwner().getId()) {
            throw new CustomException("You are not the team owner, you can't perform this action", HttpStatus.UNAUTHORIZED);
        }
    }
}
