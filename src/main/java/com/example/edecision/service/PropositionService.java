package com.example.edecision.service;

import com.example.edecision.model.amendement.Amendement;
import com.example.edecision.model.common.Parameters;
import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.project.Project;
import com.example.edecision.model.proposition.*;
import com.example.edecision.model.userProposition.UserProposition;
import com.example.edecision.model.comment.Comment;
import com.example.edecision.model.vote.CommentVote;
import com.example.edecision.model.vote.JudgeVoteResult;
import com.example.edecision.model.proposition.PropositionStatus;
import com.example.edecision.model.vote.PropositionVote;
import com.example.edecision.model.vote.VoteType;
import com.example.edecision.repository.amendement.AmendementRepository;
import com.example.edecision.repository.comment.CommentRepository;
import com.example.edecision.repository.project.ProjectRepository;
import com.example.edecision.repository.vote.CommentVoteRepository;
import com.example.edecision.repository.vote.PropositionVoteRepository;
import com.example.edecision.repository.vote.VoteTypeRepository;
import com.example.edecision.service.TeamService;
import com.example.edecision.service.UserService;
import com.example.edecision.utils.Common;
import com.example.edecision.model.team.Team;
import com.example.edecision.model.user.User;
import com.example.edecision.repository.proposition.PropositionRepository;
import com.example.edecision.repository.proposition.PropositionStatusRepository;
import com.example.edecision.repository.team.TeamRepository;
import com.example.edecision.repository.teamProposition.TeamPropositionRepository;
import com.example.edecision.repository.user.UserRepository;
import com.example.edecision.repository.userProposition.UserPropositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    public CommentRepository commentRepo;

    @Autowired
    public CommentVoteRepository commentVoteRepo;

    @Autowired
    public VoteTypeRepository voteTypeRepo;

    @Autowired
    public AmendementRepository amendementRepo;

    @Autowired
    public PropositionVoteRepository propositionVoteRepo;

    @Autowired
    public UserService userService;

    @Autowired
    public TeamService teamService;

    // ===================
    // === Proposition ===
    // ===================

    /**
     * Permet de récupérer toutes les propositions auxquels l'utilisateur courant est rattaché
     *
     * @return liste de proposition
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

        // Vérification que la proposition existe et qu'elle est visible pour cet utilisateur
        if (optionalProposition.isEmpty())
            throw new CustomException("This proposition doesn't exists", HttpStatus.NOT_FOUND);

        Proposition proposition = optionalProposition.get();

        // Récupération et affectation des utilisateurs et teams
        List<Team> teamPropositionList = teamService.getTeamsByProposition(propositionId);
        List<User> users = userRepo.getUsersByProposition(proposition.getId());

        proposition.setTeams(teamPropositionList);
        proposition.setUsers(users);

        // Permet de savoir, pour un utilisateur, si la proposition peut être modifiée et/ou votée
        boolean isUserInTeamsProposition = userService.isUserInTeams(currentUser.getId(), teamPropositionList);
        boolean isUserInUsersProposition = users.stream().anyMatch(u -> u.getId() == currentUser.getId());

        proposition.setIsEditable(proposition.getEndTime().getTime() > System.currentTimeMillis() && proposition.getPropositionStatus().getId() == 1 && (isUserInTeamsProposition || isUserInUsersProposition) && checkAmendDelay(proposition, false, true));
        proposition.setIsVotable(proposition.getEndTime().getTime() > System.currentTimeMillis() && proposition.getPropositionStatus().getId() == 1 && (isUserInTeamsProposition || isUserInUsersProposition) && checkAmendDelay(proposition, true, false));

        return proposition;
    }

    /**
     * Permet de créer une proposition dans un projet
     *
     * @param projectId       id du projet
     * @param propositionBody objet de la proposition
     * @return la proposition créée
     */
    public Proposition createProjectProposition(int projectId, PropositionBody propositionBody) {

        // Utilisateur qui demande à créer la proposition
        User currentUser = Common.GetCurrentUser();

        // Variables qui vont nous servir pour initialiser la date de création et vérifier la date de fin
        Date now = Common.GetCurrentLocalDate();
        long endDateMillisDiff = Math.abs(propositionBody.getProposition().getEndTime().getTime() - now.getTime());
        long endDateDayDiff = TimeUnit.DAYS.convert(endDateMillisDiff, TimeUnit.MILLISECONDS);

        Optional<Project> optionalProject = projectRepo.findById(projectId);
        Optional<Proposition> optionalLastProposition = propositionRepo.getLastPropositionByUserId(currentUser.getId());

        // Vérifie que le projet existe
        if (optionalProject.isEmpty())
            throw new CustomException("This project doesn't exists", HttpStatus.BAD_REQUEST);

        // Vérifie que la date de fin soit dans 1 semaine au plus tôt et 1 mois au plus tard
        if (endDateDayDiff < 7 || endDateDayDiff > 31)
            throw new CustomException("The decision end date must be between 7 and 31 days after the creation of the proposal", HttpStatus.BAD_REQUEST);

        // Vérifie que l'utilisateur a déjà été dans une proposition
        if (optionalLastProposition.isPresent()) {

            Proposition lastProposition = optionalLastProposition.get();
            long lastPropositionMillisDiff = Math.abs(now.getTime() - lastProposition.getBeginTime().getTime());
            long lastPropositionDayDiff = TimeUnit.DAYS.convert(lastPropositionMillisDiff, TimeUnit.MILLISECONDS);

            // Vérifie si l'utilisateur fait déjà partie d'une proposition créée il y a moins de 7 jours
            if (lastPropositionDayDiff <= Parameters.DAYS_BETWEEN_TWO_PROPOSITIONS)
                throw new CustomException("You already belong to a proposal created less than a week ago", HttpStatus.FORBIDDEN);
        }

        // Prend le délai d'amendement par défaut s'il n'y en a pas dans le body
        if (propositionBody.getProposition().getAmendmentDelay() <= 0)
            propositionBody.getProposition().setAmendmentDelay(Parameters.PROPOSITION_AMENDMENT_DELAY);

        propositionBody.getProposition().setBeginTime(now);
        propositionBody.getProposition().setProject(optionalProject.get());

        // Une proposition qui vient d'être créée est forcément au statut "en cours"
        propositionBody.getProposition().setPropositionStatus(propositionStatusRepo.findById(1).get());

        // Création de la proposition
        Proposition createdProposition = propositionRepo.save(propositionBody.getProposition());

        // Affectation des utilisateurs qui sont à la base de la création de cette proposition, il s'agit des gestionnaires
        createUsersProposition(propositionBody.getUserIdList(), createdProposition.getId());

        // Affectation de l'équipe qui gère la proposition
        teamPropositionRepo.createTeamProposition(createdProposition.getId(), propositionBody.getTeam().getId());

        return getProjectPropositionById(projectId, createdProposition.getId());
    }

    /**
     * Permet de modifier une proposition qui appartient à un projet, pendant son délai d'amendement
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

        // Vérification que l'utilisateur qui veut modifier la proposition soit un des créateurs
        if (oldUsers.stream().noneMatch(u -> u.getId() == currentUser.getId()))
            throw new CustomException("You do not have the right to modify this proposal", HttpStatus.FORBIDDEN);

        // Vérification statut en cours de la proposition et qu'on soit dans le délai d'amendement
        if (oldProposition.getPropositionStatus().getId() != 1 || !checkAmendDelay(oldProposition, false, true))
            throw new CustomException("You no longer have the right to modify this proposal", HttpStatus.FORBIDDEN);

        // Modification des propriétés dans l'objet
        oldProposition.setTitle(propositionBody.getProposition().getTitle());
        oldProposition.setEndTime(propositionBody.getProposition().getEndTime());
        oldProposition.setAmendmentDelay(propositionBody.getProposition().getAmendmentDelay());
        oldProposition.setContent(propositionBody.getProposition().getContent());

        // Modification des champs en base de données
        propositionRepo.save(oldProposition);

        // Création des nouveaux gestionnaires
        if (propositionBody.getUserIdList() != null) {
            for (int userId : propositionBody.getUserIdList()) {
                if (oldUsers.stream().noneMatch(x -> x.getId() == userId)) {
                    userPropositionRepo.save(new UserProposition(userId, propositionId));
                }
            }
        }

        // Suppression des utilisateurs qui ne doivent plus être gestionnaires de celle-ci, hormis celui qui est en train de la modifier
        for (User user : oldUsers) {
            if ((propositionBody.getUserIdList() == null || propositionBody.getUserIdList().stream().noneMatch(x -> x == user.getId())) && user.getId() != currentUser.getId()) {
                userPropositionRepo.deleteUserProposition(propositionId, user.getId());
            }
        }

        // Retourne la proposition modifiée
        return getProjectPropositionById(projectId, oldProposition.getId());
    }

    /**
     * Permet d'escalader une proposition, actuellement seulement d'une proposition → à toutes celles du projet (1 niveau d'escalade au lieu de 2)
     *
     * @param projectId     id du projet
     * @param propositionId id de la proposition
     */
    public EscalatePropositionResult escalateProjectProposition(int projectId, int propositionId) {
        // Objet de retour
        EscalatePropositionResult result = new EscalatePropositionResult();

        // Récupération de l'utilisateur qui veut escalader la proposition
        User currentUser = Common.GetCurrentUser();

        // Permet de récupérer la proposition et générer une erreur si elle n'existe pas
        Proposition proposition = getProjectPropositionById(projectId, propositionId);
        result.setTeams(proposition.getTeams());

        // On vérifie que la proposition n'a pas déjà été escaladée
        if (proposition.getTeams().size() > 1)
            throw new CustomException("The proposal has already been escalated", HttpStatus.FORBIDDEN);

        // On vérifie que l'utilisateur soit dans une équipe
        if (!userService.isUserInTeams(currentUser.getId(), proposition.getTeams()))
            throw new CustomException("You do not have the right to escalate this proposal", HttpStatus.FORBIDDEN);

        // Récupère le commentaire de suppression s'il existe déjà
        Comment escalatedComment;
        Optional<Comment> optionalEscalatedComment = commentRepo.getPropositionEscalateComment(projectId, propositionId);

        if (optionalEscalatedComment.isPresent()) { // Si un utilisateur a déjà fait la proposition d'escalade

            // On récupère le commentaire d'escalade
            escalatedComment = optionalEscalatedComment.get();

            // On récupère les votes déjà existants sur ce commentaire d'escalade
            List<CommentVote> commentVotes = commentVoteRepo.getCommentVotes(escalatedComment.getId());

            // On vérifie si l'utilisateur courant a déjà voté ou non pour l'escalade
            if (commentVotes != null && commentVotes.stream().anyMatch(v -> v.getUserId() == currentUser.getId()))
                throw new CustomException("You have already voted to escalate this proposal", HttpStatus.FORBIDDEN);
        } else {
            // On crée le commentaire d'escalade
            escalatedComment = commentRepo.save(new Comment(propositionId, true, false, currentUser));
        }

        // On ajoute le vote de l'utilisateur courant
        commentVoteRepo.save(new CommentVote(currentUser.getId(), escalatedComment.getId(), voteTypeRepo.getById(1)));

        // On récupère la liste des votes pour ce commentaire de suppression
        List<CommentVote> commentVotes = commentVoteRepo.getCommentVotes(escalatedComment.getId());

        // On récupère la liste des utilisateurs qui doivent voter pour ce commentaire afin que la prop soit escaladée
        List<User> propositionTeamUsers = new ArrayList<>();
        for (Team team : proposition.getTeams()) {
            propositionTeamUsers.addAll(team.getUsers()); // Ne marche pas en lambda...
        }
        propositionTeamUsers = propositionTeamUsers.stream().distinct().collect(Collectors.toList());

        // On récupère les utilisateurs qui veulent escalader
        result.setVoteUsers(propositionTeamUsers.stream().filter(u -> commentVotes.stream().anyMatch(v -> v.getUserId() == u.getId())).collect(Collectors.toList()));

        // On récupère les utilisateurs qui ne veulent pas encore escalader
        result.setNotVoteUsers(propositionTeamUsers.stream().filter(u -> commentVotes.stream().noneMatch(v -> v.getUserId() == u.getId())).collect(Collectors.toList()));

        // On regarde si tous les utilisateurs veulent escalader
        result.setEscalated(result.getNotVoteUsers().size() == 0);

        // Si tout le monde est ok pour escalader alors on ajoute toutes les teams du projet à la proposition
        if (result.isEscalated()) {
            List<Team> projectTeams = teamRepo.getTeamsByProject(projectId);
            projectTeams.stream().filter(t -> !proposition.getTeams().contains(t)).forEach(t -> teamPropositionRepo.createTeamProposition(propositionId, t.getId()));
            result.setTeams(projectTeams);
        }

        return result;
    }

    /**
     * Permet de supprimer et/ou proposer la suppression d'une proposition, il faut que toutes les personnes qui l'ont créées soit ok
     *
     * @param projectId     id du projet
     * @param propositionId id de la proposition
     */
    public DeletePropositionResult deleteProjectProposition(int projectId, int propositionId) {
        // Objet de retour
        DeletePropositionResult result = new DeletePropositionResult();

        // Récupération de l'utilisateur qui veut supprimer la proposition
        User currentUser = Common.GetCurrentUser();

        // Permet de récupérer la proposition et générer une erreur si elle n'existe pas
        Proposition proposition = getProjectPropositionById(projectId, propositionId);

        // On vérifie que l'utilisateur soit un des créateurs
        if (proposition.getUsers().stream().noneMatch(u -> u.getId() == currentUser.getId()))
            throw new CustomException("You do not have the right to delete this proposal", HttpStatus.FORBIDDEN);

        // Récupère le commentaire de suppression s'il existe déjà
        Comment deletedComment;
        Optional<Comment> optionalDeletedComment = commentRepo.getPropositionDeletedComment(projectId, propositionId);

        if (optionalDeletedComment.isPresent()) { // Si un utilisateur gestionnaire de la prop a déjà fait une proposition de suppression

            // On récupère le commentaire de suppression
            deletedComment = optionalDeletedComment.get();

            // On récupère les votes déjà existants sur ce commentaire de suppression
            List<CommentVote> commentVotes = commentVoteRepo.getCommentVotes(deletedComment.getId());

            // On vérifie si l'utilisateur courant a déjà voté ou non pour la suppression
            if (commentVotes != null && commentVotes.stream().anyMatch(v -> v.getUserId() == currentUser.getId()))
                throw new CustomException("You have already voted to delete this proposal", HttpStatus.FORBIDDEN);

        } else {
            // On crée le commentaire de suppression
            deletedComment = commentRepo.save(new Comment(propositionId, false, true, currentUser));
        }

        // On ajoute le vote de l'utilisateur courant
        commentVoteRepo.save(new CommentVote(currentUser.getId(), deletedComment.getId(), voteTypeRepo.getById(1)));

        // On récupère la liste des votes pour ce commentaire de suppression
        List<CommentVote> commentVotes = commentVoteRepo.getCommentVotes(deletedComment.getId());

        // On récupère la liste des utilisateurs qui doivent voter pour ce commentaire afin que la prop soit supprimée
        List<User> propositionUsers = proposition.getUsers();

        // On récupère les utilisateurs qui ont voté
        result.setVoteUsers(propositionUsers.stream().filter(u -> commentVotes.stream().anyMatch(v -> v.getUserId() == u.getId())).collect(Collectors.toList()));

        // On récupère les utilisateurs qui n'ont pas voté
        result.setNotVoteUsers(propositionUsers.stream().filter(u -> commentVotes.stream().noneMatch(v -> v.getUserId() == u.getId())).collect(Collectors.toList()));

        // On regarde si tous les utilisateurs ont voté
        result.setDeleted(result.getNotVoteUsers().size() == 0);

        if (result.isDeleted()) { // Si tous les utilisateurs ont voté
            userPropositionRepo.deleteUserPropositionsByProposition(propositionId);
            teamPropositionRepo.deleteTeamPropositionsByProposition(propositionId);
            amendementRepo.deleteAmendementsByProposition(propositionId);
            commentVoteRepo.deleteCommentVotesByProposition(propositionId);
            commentRepo.deleteCommentsByProposition(propositionId);
            propositionVoteRepo.deleteProjectPropositionVotesById(propositionId);
            propositionRepo.deleteProposition(propositionId);
        }

        return result;
    }

    /**
     * Demo - Permet de forcer le statut d'une proposition afin qu'on puisse la voter
     *
     * @param projectId       id du projet
     * @param propositionId   id de la proposition
     * @param propositionBody objet de la proposition
     * @return la proposition mise à jour
     */
    public Proposition updateDemoProjectPropositionStatusById(int projectId, int propositionId) {

        // Récupération de la proposition et génération d'exception si elle ou le projet n'existe pas
        Proposition oldProposition = getProjectPropositionById(projectId, propositionId);

        // Conversion de la date en objet Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(oldProposition.getBeginTime());

        // Reculer la date de début
        calendar.add(Calendar.DAY_OF_YEAR, -(oldProposition.getAmendmentDelay() + 1));
        oldProposition.setBeginTime(calendar.getTime());

        // Modification des champs en base de données
        propositionRepo.save(oldProposition);

        // Retourne la proposition modifiée
        return getProjectPropositionById(projectId, oldProposition.getId());
    }

    // ================================
    // === Project proposition vote ===
    // ================================

    /**
     * Permet de récupérer les votes d'une proposition
     *
     * @param projectId     id du projet
     * @param propositionId id de la proposition
     */
    public List<PropositionVote> getProjectPropositionVotesById(int projectId, int propositionId) {

        // Permet de générer une erreur si la proposition n'existe pas
        getProjectPropositionById(projectId, propositionId);

        return propositionVoteRepo.getProjectPropositionVotesById(projectId, propositionId);
    }

    /**
     * Permet de voter une proposition si l'utilisateur a le droit et que c'est dans les délais
     *
     * @param projectId     id du projet
     * @param propositionId id de la proposition
     * @param body          vote de l'utilisateur
     */
    public List<PropositionVote> voteProjectProposition(int projectId, int propositionId, PropositionVoteBody body) {

        // Récupération de l'utilisateur qui veut voter la proposition
        User currentUser = Common.GetCurrentUser();

        // Permet de récupérer la proposition et générer une erreur si elle n'existe pas
        Proposition proposition = getProjectPropositionById(projectId, propositionId);

        // On vérifie que la proposition soit en période de vote et accessible par l'utilisateur
        if (!proposition.getIsVotable())
            throw new CustomException("You do not have the right to vote this proposal", HttpStatus.FORBIDDEN);

        // On récupère la liste des votes déjà existants
        List<PropositionVote> propositionVotes = propositionVoteRepo.getProjectPropositionVotesById(projectId, propositionId);

        // On vérifie que l'utilisateur n'ait pas déjà voté pour cette proposition
        if (propositionVotes != null && propositionVotes.stream().anyMatch(v -> v.getUser().getId() == currentUser.getId()))
            throw new CustomException("You have already voted for this proposal", HttpStatus.FORBIDDEN);

        // Si tout est bon alors on ajoute son vote
        propositionVoteRepo.createProjectPropositionVote(currentUser.getId(), propositionId, body.getVoteType().getId());

        return propositionVoteRepo.getProjectPropositionVotesById(projectId, propositionId);
    }

    /**
     * Permet de juger les votes d'une proposition et de ses amendements pour savoir ce qui est validé/refusé
     *
     * @param projectId     id du projet
     * @param propositionId id de la proposition
     */
    public JudgeVoteResult judgeProjectProposition(int projectId, int propositionId) {
        // Objet de retour
        JudgeVoteResult result = new JudgeVoteResult();

        // Variable qui va nous servir à savoir s'il est temps de juger la proposition
        Date now = Common.GetCurrentLocalDate();

        // Récupération de l'utilisateur qui veut supprimer la proposition
        User currentUser = Common.GetCurrentUser();

        // Permet de récupérer la proposition et générer une erreur si elle n'existe pas
        Proposition proposition = getProjectPropositionById(projectId, propositionId);

        // On vérifie que la proposition n'ait pas déjà été jugée
        if (proposition.getPropositionStatus().getId() > 1)
            throw new CustomException("The proposal has already been judged", HttpStatus.FORBIDDEN);

        // On vérifie que la proposition ne soit plus dans les délais d'amendement / de votes
        if (proposition.getEndTime().after(now))
            throw new CustomException("It is not yet time to judge this proposal and its amendments", HttpStatus.FORBIDDEN);

        // On vérifie que l'utilisateur soit un des créateurs
        if (proposition.getUsers().stream().noneMatch(u -> u.getId() == currentUser.getId()))
            throw new CustomException("You do not have the right to judge this proposal", HttpStatus.FORBIDDEN);

        // On récupère tous les amendements à juger avec la proposition
        List<Amendement> propositionAmendements = amendementRepo.getProjectPropositionAmendements(projectId, propositionId);

        // Gestion de la proposition
        proposition.setPropositionStatus(propositionStatusRepo.getById(judgeVotes(propositionVoteRepo.getProjectPropositionVotesById(projectId, propositionId), proposition.getTeams())));
        propositionRepo.save(proposition);

        // Gestion des amendements
        propositionAmendements.forEach(a -> {
            a.setAmendementStatus(propositionStatusRepo.getById(judgeVotes(propositionVoteRepo.getProjectPropositionAmendementVotesById(projectId, propositionId, a.getId()), proposition.getTeams())));
            amendementRepo.save(a);
        });

        result.setProposition(getProjectPropositionById(projectId, propositionId));
        return result;
    }

    // =============
    // === Utils ===
    // =============

    private void createUsersProposition(List<Integer> users, int proposition) {
        int currentUserId = Common.GetCurrentUser().getId();
        if(users == null) users = new ArrayList<Integer>();
        if (!users.contains(currentUserId)) users.add(currentUserId);
        for (int user : users) {
            userPropositionRepo.createUserProposition(proposition, user);
        }
    }

    private boolean checkAmendDelay(Proposition proposition, boolean errorIfInsideDelay, boolean errorIfOutsideDelay) {

        // Variable qui va servir a vérifier les dates
        Date now = Common.GetCurrentLocalDate();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(proposition.getBeginTime());
        calendar.add(Calendar.DATE, proposition.getAmendmentDelay());
        Date maxDateForUpdate = calendar.getTime();

        // Vérification qu'il est encore temps de modifier la proposition à partir de la date de création et délai d'amendement et statut
        return !(proposition.getPropositionStatus().getId() != 1 || (maxDateForUpdate.before(now) && errorIfOutsideDelay) || (maxDateForUpdate.after(now) && errorIfInsideDelay));
    }

    /**
     * Détermine, avec les votes, si une proposition ou un amendement est accepté ou décliné
     *
     * @param votes votes
     * @param teams équipes concernées
     */
    private int judgeVotes(List<PropositionVote> votes, List<Team> teams) {
        int validatedStatus = 2;
        int declinedStatus = 3;

        int totalVote = votes.size();
        int totalVoteOk = (int) votes.stream().filter(v -> v.getVoteType().getId() == 1).count();
        int minVotes;

        // Le niveau à la communauté n'est pas encore géré

        if (teams.size() == 1) // Une proposition à l'échelle d'une équipe sera adoptée à la majorité simple (50%+1 voix pour)
        {
            minVotes = (int) (totalVote * 0.5) + 1;
        } else // À l'échelle d'un projet à la majorité qualifiée de 60% (60%+1 voix pour)
        {
            minVotes = (int) (totalVote * 0.6) + 1;
        }

        if (totalVoteOk >= minVotes)
            return validatedStatus;
        else
            return declinedStatus;
    }

    // ==========================
    // === Proposition status ===
    // ==========================

    /**
     * Permet de récupérer la liste des statuts de proposition
     *
     * @return la liste des statuts de proposition
     */
    public List<PropositionStatus> getPropositionStatus() {
        return propositionStatusRepo.findAll();
    }

    // =================
    // === Vote type ===
    // ==================

    /**
     * Permet de récupérer la liste des types de vote
     *
     * @return la liste des types de vote
     */
    public List<VoteType> getVoteTypes() {
        return voteTypeRepo.findAll();
    }

}
