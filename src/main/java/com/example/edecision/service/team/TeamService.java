package com.example.edecision.service.team;

import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.team.Team;
import com.example.edecision.model.team.TeamBody;
import com.example.edecision.model.team.TeamType;
import com.example.edecision.model.team.UserTeam;
import com.example.edecision.model.user.User;
import com.example.edecision.repository.team.TeamRepository;
import com.example.edecision.repository.team.TeamTypeRepository;
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
    public TeamRepository teamRepo;

    @Autowired
    public UserTeamRepository userTeamRepo;

    @Autowired
    public UserRepository userRepo;

    @Autowired
    public TeamTypeRepository teamTypeRepo;

    // ============
    // === Team ===
    // ============

    /**
     * Récupère toutes les équipes
     * @return la liste des équipes
     */
    public List<Team> getAllTeams() {
        return teamRepo.findAll();
    }

    /**
     * Récupère une équipe avec son identifiant
     *
     * @param id id de l'équipe
     * @return l'équipe
     */
    public Team getTeamById(int id) {
        return teamRepo.findById(id).orElseThrow(() -> new CustomException("No team found with this id : " + id, HttpStatus.NOT_FOUND));
    }

    /**
     * Permet de créer une équipe en associant tous ses utilisateurs
     *
     * @param teamBody objet de l'équipe et l'association de ses utilisateurs
     * @return l'équipe créée
     */
    public Team createTeam(TeamBody teamBody) {
        if (teamRepo.findByName(teamBody.getTeam().getName()).isPresent()) {
            throw new CustomException("A team with this name already exists", HttpStatus.CONFLICT);
        }
        User owner = Common.GetCurrentUser();
        teamBody.getTeam().setOwner(owner);
        List<User> usersToAddInTeam = userRepo.getUsersById(teamBody.getUserIdList());
        if (usersToAddInTeam.size() < teamBody.getUserIdList().size()) {
            throw new CustomException("One user or more that you have provided don't exist", HttpStatus.NOT_FOUND);
        }
        Team createdTeam = teamRepo.save(teamBody.getTeam());
        teamBody.getUserIdList().forEach((userId) -> {
            UserTeam userTeam = new UserTeam(userId, createdTeam.getId());
            userTeamRepo.save(userTeam);
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
        if (teamRepo.findById(teamId).isPresent()) {
            Team updatedTeam = teamRepo.findById(teamId).get();
            verifyUserOwnership(updatedTeam);
            if (teamRepo.findByName(teamBody.getTeam().getName()).isPresent()) {
                Team foundTeam = teamRepo.findByName(teamBody.getTeam().getName()).get();
                if (foundTeam.getId() != teamId) {
                    throw new CustomException("A team with this name already exists", HttpStatus.CONFLICT);
                }
            }
            List<User> oldUserList = userRepo.findAllUsersByTeamId(teamId);

            modifyUsersInTeam(teamId, teamBody, oldUserList);

            updatedTeam.setName(teamBody.getTeam().getName());
            updatedTeam.setTeamType(teamBody.getTeam().getTeamType());
            updatedTeam.setOwner(Common.GetCurrentUser());
            return teamRepo.save(updatedTeam);
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
        if (teamRepo.findById(id).isPresent()) {
            Team deletedTeam = teamRepo.findById(id).get();
            verifyUserOwnership(deletedTeam);
            userTeamRepo.deleteAllUserTeam(id);
            teamRepo.deleteById(id);
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
        List<Team> teamList = teamRepo.getFreeTeamsForProjectCreation(teamsIds);
        return getTeamsWithUsers(teamList);
    }

    /**
     * Permet toutes les équipes d'une proposition
     *
     * @param propositionId id de la proposition
     * @return la liste des équipes rattachées à la proposition avec leurs utilisateurs
     */
    public List<Team> getTeamsByProposition(int propositionId) {
        List<Team> teamList = teamRepo.getTeamsByProposition(propositionId);
        return getTeamsWithUsers(teamList);
    }

    /**
     * Permet de retourner les équipes avec les utilisateurs associés
     *
     * @param teamList liste d'équipes
     * @return liste d'équipes avec utilisateurs associés
     */
    private List<Team> getTeamsWithUsers(List<Team> teamList) {
        teamList.forEach(team -> team.setUsers(userRepo.findAllUsersByTeamId(team.getId())));
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
                userTeamRepo.save(new UserTeam(userId, teamId));
            }
        });

        oldUserList.forEach(user -> {
            if (teamBody.getUserIdList().stream().noneMatch(userId -> userId == user.getId())) {
                userTeamRepo.deleteUserTeam(user.getId(), teamId);
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

    // =================
    // === Team type ===
    // =================

    /**
     * Permet de récupérer la liste des types d'équipe
     *
     * @return la liste des types d'équipe
     */
    public List<TeamType> getTeamTypes(){
        return teamTypeRepo.findAll();
    }
}
