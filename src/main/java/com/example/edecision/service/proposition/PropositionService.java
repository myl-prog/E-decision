package com.example.edecision.service.proposition;

import com.example.edecision.model.common.Parameters;
import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.project.Project;
import com.example.edecision.model.proposition.PropositionBody;
import com.example.edecision.model.proposition.PropositionStatus;
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
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
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
     * Get a project proposition by id for current user
     *
     * @param projectId     projectId
     * @param propositionId propositionId
     * @return a proposition
     */
    public Proposition getProjectPropositionById(int projectId, int propositionId) {
        User user = Common.GetCurrentUser();
        Optional<Proposition> optionalProposition = propositionRepo.getProjectPropositionById(projectId, propositionId);
        if (optionalProposition.isPresent()) {
            Proposition proposition = optionalProposition.get();
            List<Team> projectTeams = teamService.getTeamsByProject(projectId);
            if (userService.isUserInTeams(user.getId(), projectTeams)) {
                List<Team> teamPropositionList = teamService.getTeamsByProposition(propositionId);
                boolean isUserInTeamsProposition = userService.isUserInTeams(user.getId(), teamPropositionList);
                List<User> users = userRepo.getUsersByProposition(proposition.getId());
                proposition.setTeams(teamPropositionList);
                proposition.setUsers(users);

                proposition.setIsEditable(proposition.getEnd_time().getTime() >= System.currentTimeMillis() && isUserInTeamsProposition);
                proposition.setIsVoteable(proposition.getEnd_time().getTime() < System.currentTimeMillis() && proposition.getProposition_status().getId() == 1 && isUserInTeamsProposition);
                return proposition;
            } else {
                throw new CustomException("You have not access to this proposition", HttpStatus.UNAUTHORIZED);
            }
        } else {
            throw new CustomException("This proposition doesn't exists", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Permet de créer une proposition dans un projet
     *
     * @param projectId       projectId
     * @param propositionBody propositionBody
     * @return the created proposition
     */
    public Proposition createProposition(int projectId, PropositionBody propositionBody)
    {
        // Utilisateur qui demande à créer la proposition
        User user = Common.GetCurrentUser();

        // Variables qui vont nous servir pour initialiser la date de création et vérifier la date de fin
        LocalDateTime localDateTime = LocalDateTime.now();
        Date now = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        long endDateMilliesDiff = Math.abs(propositionBody.proposition.getEnd_time().getTime() - now.getTime());
        long endDateDayDiff = TimeUnit.DAYS.convert(endDateMilliesDiff, TimeUnit.MILLISECONDS);

        Optional<Project> optionalProject = projectRepo.findById(projectId);
        Optional<Proposition> optionalLastProposition = propositionRepo.getLastPropositionByUserId(user.getId());

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
        propositionBody.proposition.setProposition_status(new PropositionStatus(1));

        // Création de la proposition
        Proposition createdProposition = propositionRepo.save(propositionBody.proposition);

        // Affectation des utilisateurs qui sont à la base de la création de cette proposition, il s'agit des gestionnaires
        createUsersProposition(propositionBody.users, createdProposition.getId());

        // Affectation de l'équipe qui est en charge de la proposition
        teamPropositionRepo.createTeamProposition(createdProposition.getId(), propositionBody.team.getId());

        return createdProposition;
    }

    /*public Proposition updateProjectPropositionById(int projectId, int propositionId, PropositionBody propositionBody) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        // Todo : check user and check if we are in amandment delay, end time etc
        Proposition oldProposition = getProjectPropositionById(projectId, propositionId);

        List<Team> oldTeams = oldProposition.getTeams();
        List<User> oldUsers = oldProposition.getUsers();
        oldProposition.setTeams(null);
        oldProposition.setUsers(null);

        // Save in proposition table
        oldProposition.setTitle(propositionBody.proposition.getTitle());
        oldProposition.setEnd_time(propositionBody.proposition.getEnd_time());
        oldProposition.setAmendment_delay(propositionBody.proposition.getAmendment_delay());
        oldProposition.setContent(propositionBody.proposition.getContent());
        oldProposition.setAmend_proposition(propositionBody.proposition.getAmend_proposition());

        propositionRepo.save(oldProposition);

        // users to create in user_proposition
        if (propositionBody.users != null) {
            for (int userId : propositionBody.users) {
                if (oldUsers == null || !oldUsers.stream().anyMatch(x -> x.getId() == userId)) {
                    userPropositionRepo.save(new UserProposition(userId, propositionId));
                }
            }
        }

        // users to delete in user_proposition
        if (oldUsers != null) {
            for (User user : oldUsers) {
                if (propositionBody.users == null || !(IntStream.of(propositionBody.users).anyMatch(x -> x == user.getId()))) {
                    userPropositionRepo.deleteUserProposition(propositionId, user.getId());
                }
            }
        }

        // teams to create in team_proposition
        if (propositionBody.teams != null) {
            for (int teamId : propositionBody.teams) {
                if (oldTeams == null || !oldTeams.stream().anyMatch(x -> x.getId() == teamId)) {
                    teamPropositionRepo.save(new TeamProposition(teamId, propositionId));
                }
            }
        }

        // teams to delete in team_proposition
        if (oldTeams != null) {
            for (Team team : oldTeams) {
                if (propositionBody.teams == null || !(IntStream.of(propositionBody.teams).anyMatch(x -> x == team.getId()))) {
                    teamPropositionRepo.deleteTeamProposition(propositionId, team.getId());
                }
            }
        }

        return getProjectPropositionById(oldProposition.getId());
    }*/

    /*public Proposition amend(int amendPropositionId, AmendPropositionBody body) {

        User user = Common.GetCurrentUser();
        Proposition amendProposition = propositionRepo.getProjectPropositionById(amendPropositionId, user.getId());

        if (amendProposition != null) {
            Proposition proposition = new Proposition();
            proposition.setTitle(body.getTitle());
            proposition.setContent(body.getContent());
            proposition.setAmend_proposition(amendProposition);
            proposition.setEnd_time(amendProposition.getEnd_time());
            return propositionRepo.save(proposition);
        } else {
            // ERROR
            return null;
        }

    }*/

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

}
