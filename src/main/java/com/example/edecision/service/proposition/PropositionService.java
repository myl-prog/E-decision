package com.example.edecision.service.proposition;

import com.example.edecision.model.common.Parameters;
import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.project.Project;
import com.example.edecision.model.proposition.PropositionBody;
import com.example.edecision.model.userProposition.UserProposition;
import com.example.edecision.repository.project.ProjectRepository;
import com.example.edecision.service.team.TeamService;
import com.example.edecision.service.user.UserService;
import com.example.edecision.utils.Common;
import com.example.edecision.model.team.Team;
import com.example.edecision.model.user.User;
import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.repository.proposition.PropositionRepository;
import com.example.edecision.repository.proposition.PropositionStatusRepository;
import com.example.edecision.repository.team.TeamRepository;
import com.example.edecision.repository.teamProposition.TeamPropositionRepository;
import com.example.edecision.repository.user.UserRepository;
import com.example.edecision.repository.userProposition.UserPropositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PropositionService {

    @Autowired
    public PropositionRepository propositionRepo;

    @Autowired
    public PropositionStatusRepository propositionStatusRepo;

    @Autowired
    public UserPropositionRepository userPropositionRepo;

    @Autowired
    public TeamPropositionRepository teamPropositionRepo;

    @Autowired
    public UserRepository userRepo;

    @Autowired
    public TeamRepository teamRepo;

    @Autowired
    public ProjectRepository projectRepo;

    @Autowired
    public UserService userService;

    @Autowired
    public TeamService teamService;



    /**
     * Get all propositions where current user is associated
     *
     * @return The proposition list associated to current user
     */
    public List<Proposition> getAllPropositionsByUser() {
        User user = Common.GetCurrentUser();
        return propositionRepo.getPropositionsByUser(user.getId());
    }

    /**
     * Récupère une proposition qui est dans un projet
     *
     * @param projectId     id du projet
     * @param propositionId id de la proposition
     * @return la proposition
     */
    public Proposition getProjectPropositionById(int projectId, int propositionId) {

        // Récupération de l'utilisateur qui veut accéder à la proposition
        User currentUser = Common.GetCurrentUser();

        // Récupération de la proposition
        Optional<Proposition> optionalProposition = propositionRepo.getProjectPropositionById(projectId, propositionId);

        // Vérification que la proposition existe et qu'elle est visible pour ce user
        if(!optionalProposition.isPresent())
            throw new CustomException("This proposition doesn't exists", HttpStatus.NOT_FOUND);

        Proposition proposition = optionalProposition.get();

        // Récupération et affectation des utilisateurs et teams
        List<Team> teamPropositionList = teamService.getTeamsByProposition(propositionId);
        List<User> users = userRepo.getUsersByProposition(proposition.getId());

        proposition.setTeams(teamPropositionList);
        proposition.setUsers(users);

        // Permet de savoir, pour un utilisateur, si la proposition peut être modifiée et/ou votée
        boolean isUserInTeamsProposition = userService.isUserInTeams(currentUser.getId(), teamPropositionList);
        proposition.setIsEditable(proposition.getEnd_time().getTime() > System.currentTimeMillis() && proposition.getProposition_status().getId() == 1 && isUserInTeamsProposition && checkAmendDelay(proposition, false, true));
        proposition.setIsVoteable(proposition.getEnd_time().getTime() > System.currentTimeMillis() && proposition.getProposition_status().getId() == 1 && isUserInTeamsProposition && checkAmendDelay(proposition, true, false));

        return proposition;
    }

    /**
     * Permet de créer une proposition dans un projet
     *
     * @param projectId       id du projet
     * @param propositionBody objet de la proposition
     * @return la proposition créée
     */
    public Proposition createProjectProposition(int projectId, PropositionBody propositionBody)
    {
        // Utilisateur qui demande à créer la proposition
        User currentUser = Common.GetCurrentUser();

        // Variables qui vont nous servir pour initialiser la date de création et vérifier la date de fin
        LocalDateTime localDateTime = LocalDateTime.now();
        Date now = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        long endDateMilliesDiff = Math.abs(propositionBody.proposition.getEnd_time().getTime() - now.getTime());
        long endDateDayDiff = TimeUnit.DAYS.convert(endDateMilliesDiff, TimeUnit.MILLISECONDS);

        Optional<Project> optionalProject = projectRepo.findById(projectId);
        Optional<Proposition> optionalLastProposition = propositionRepo.getLastPropositionByUserId(currentUser.getId());

        // Vérifie que le projet existe
        if(!optionalProject.isPresent())
            throw new CustomException("This project doesn't exists", HttpStatus.BAD_REQUEST);

        // Vérifie que la date de fin soit dans 1 semaine au plus tôt et 1 mois au plus tard
        if(endDateDayDiff < 7 || endDateDayDiff > 31)
            throw new CustomException("The decision end date must be between 7 and 31 days after the creation of the proposal", HttpStatus.BAD_REQUEST);

        // Vérifie que l'utilisateur a déjà été dans une proposition
        if(optionalLastProposition.isPresent()){

            Proposition lastProposition = optionalLastProposition.get();
            long lastPropositionMilliesDiff = Math.abs(now.getTime() - lastProposition.getBegin_time().getTime());
            long lastPropositionDayDiff = TimeUnit.DAYS.convert(lastPropositionMilliesDiff, TimeUnit.MILLISECONDS);

            // Vérifie si l'utilisateur fait déjà partie d'une propoposition créée il y a moins de 7 jours
            if(lastPropositionDayDiff <= 7)
                throw new CustomException("You already belong to a proposal created less than a week ago", HttpStatus.FORBIDDEN);
        }

        // Prend le délais d'amendement par défaut s'il y en a pas dans le body
        if (propositionBody.proposition.getAmendment_delay() <= 0)
            propositionBody.proposition.setAmendment_delay(Parameters.PROPOSITION_AMENDMENT_DELAY);

        propositionBody.proposition.setBegin_time(now);
        propositionBody.proposition.setProject(optionalProject.get());

        // Une proposition qui vient d'être créée est forcément au statut "en cours"
        propositionBody.proposition.setProposition_status(propositionStatusRepo.getById(1));

        // Création de la proposition
        Proposition createdProposition = propositionRepo.save(propositionBody.proposition);

        // Affectation des utilisateurs qui sont à la base de la création de cette proposition, il s'agit des gestionnaires
        createUsersProposition(propositionBody.users, createdProposition.getId());

        // Affectation de l'équipe qui est en charge de la proposition
        teamPropositionRepo.createTeamProposition(createdProposition.getId(), propositionBody.team.getId());

        return createdProposition;
    }

    /**
     * Permet de modifier une proposition qui appartient à un projet, pendant son délais d'amendement
     *
     * @param projectId       id du projet
     * @param propositionId   id de la proposition
     * @param propositionBody objet de la proposition
     * @return la proposition mise à jour
     */
    public Proposition updateProjectPropositionById(int projectId, int propositionId, PropositionBody propositionBody) {

        // Utilisateur qui demande à modifier la proposition
        User currentUser = Common.GetCurrentUser();

        // Récupération de la proposition et génération d'exception si elle ou le projet n'existe pas
        Proposition oldProposition = getProjectPropositionById(projectId, propositionId);

        List<User> oldUsers = oldProposition.getUsers();
        oldProposition.setUsers(null);

        // Vérification que l'utilisateur qui veut modifier la proposition fasse parti des créateurs
        if(!oldUsers.stream().anyMatch(u -> u.getId() == currentUser.getId()))
            throw new CustomException("You do not have the right to modify this proposal", HttpStatus.FORBIDDEN);

        // Vérification statut en cours de la proposition et qu'on soit dans le délai d'amendement
        if(oldProposition.getProposition_status().getId() != 1 || !checkAmendDelay(oldProposition, false, true))
            throw new CustomException("You no longer have the right to modify this proposal", HttpStatus.FORBIDDEN);

        // Modification des propriétés dans l'objet
        oldProposition.setTitle(propositionBody.proposition.getTitle());
        oldProposition.setEnd_time(propositionBody.proposition.getEnd_time());
        oldProposition.setAmendment_delay(propositionBody.proposition.getAmendment_delay());
        oldProposition.setContent(propositionBody.proposition.getContent());
        oldProposition.setAmend_proposition(propositionBody.proposition.getAmend_proposition());

        // Modification des champs en base de données
        propositionRepo.save(oldProposition);

        // Création des nouveaux gestionnaires
        if (propositionBody.users != null) {
            for (int userId : propositionBody.users) {
                if (oldUsers == null || !oldUsers.stream().anyMatch(x -> x.getId() == userId)) {
                    userPropositionRepo.save(new UserProposition(userId, propositionId));
                }
            }
        }

        // Suppression des utilisateurs qui ne doivent plus être gestionnaires de celle-ci, hormis celui qui est en train de la modifier
        if (oldUsers != null) {
            for (User user : oldUsers) {
                if ((propositionBody.users == null || !propositionBody.users.stream().anyMatch(x -> x == user.getId())) && user.getId() != currentUser.getId()) {
                    userPropositionRepo.deleteUserProposition(propositionId, user.getId());
                }
            }
        }

        // Retourne la proposition modifiée
        return getProjectPropositionById(projectId, oldProposition.getId());
    }

    /*/**
     * Permit to delete a project proposition
     *
     * @param projectId     projectId
     * @param propositionId proposition id
    public void deleteProposition(int projectId, int propositionId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()) {
            Optional<Proposition> optionalProposition = propositionRepo.findById(propositionId);
            if (optionalProposition.isPresent()) {
                User currentUser = Common.GetCurrentUser();
                if (currentUser.getId() == userRepo.getPropositionOwner(propositionId).getId()) {
                    userPropositionRepo.deleteUserPropositionsByProposition(propositionId);
                    teamPropositionRepo.deleteTeamPropositionsByProposition(propositionId);
                    propositionRepo.deleteProposition(propositionId);
                } else {
                    throw new CustomException("You can't perform this action", HttpStatus.UNAUTHORIZED);
                }
            } else {
                throw new CustomException("This proposition doesn't exists", HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new CustomException("This project doesn't exists", HttpStatus.BAD_REQUEST);
        }
    }*/

    private void createUsersProposition(List<Integer> users, int proposition) {
        int currentUserId = Common.GetCurrentUser().getId();
        if(!users.contains(currentUserId)) users.add(currentUserId);
        for (int user : users) {
            userPropositionRepo.createUserProposition(proposition, user);
        }
    }

    public boolean checkAmendDelay(Proposition proposition, boolean errorIfInsideDelay, boolean errorIfOutsideDelay){

        // Variables qui vont nous servir à vérifier les dates
        LocalDateTime localDateTime = LocalDateTime.now();
        Date now = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(proposition.getBegin_time());
        calendar.add(Calendar.DATE, proposition.getAmendment_delay());
        Date maxDateForUpdate = calendar.getTime();

        // Vérification qu'il est encore temps de modifier la proposition à partir de la date de création et délai d'amendement et statut
        if(proposition.getProposition_status().getId() != 1 || (maxDateForUpdate.before(now) && errorIfOutsideDelay) || (maxDateForUpdate.after(now) && errorIfInsideDelay))
            return false;
        else
            return true;

    }

}
