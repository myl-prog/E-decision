package com.example.edecision.service.amendement;

import com.example.edecision.model.amendement.Amendement;
import com.example.edecision.model.amendement.AmendementBody;
import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.model.proposition.PropositionVoteBody;
import com.example.edecision.model.user.User;
import com.example.edecision.model.vote.PropositionVote;
import com.example.edecision.repository.amendement.AmendementRepository;
import com.example.edecision.repository.vote.PropositionVoteRepository;
import com.example.edecision.service.proposition.PropositionService;
import com.example.edecision.service.user.UserService;
import com.example.edecision.utils.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AmendementService {

    @Autowired
    public AmendementRepository amendementRepo;
    @Autowired
    public PropositionVoteRepository propositionVoteRepo;

    @Autowired
    public PropositionService propositionService;

    @Autowired
    public UserService userService;

    // ==================
    // === Amendement ===
    // ==================

    /**
     * Permet de récupérer un amendement
     *
     * @param projectId       id du projet
     * @param propositionId   id de la proposition
     * @param amendementId    id de l'amendement
     * @return l'amendement
     */
    public Amendement getProjectPropositionAmendementById(int projectId, int propositionId, int amendementId) {

        // Récupération de l'utilisateur qui veut accéder à l'amendement
        User currentUser = Common.GetCurrentUser();

        // Récupération de l'amendement
        Optional<Amendement> optionalAmendement = amendementRepo.getProjectPropositionAmendementById(projectId, propositionId, amendementId);

        // Vérification que l'amendement existe et qu'elle est visible pour ce user
        if(!optionalAmendement.isPresent())
            throw new CustomException("This amendement doesn't exists", HttpStatus.NOT_FOUND);

        Amendement amendement = optionalAmendement.get();
        Proposition proposition = propositionService.getProjectPropositionById(projectId, propositionId);

        // Permet de savoir, pour un utilisateur, si l'amendement peut être modifiée et/ou votée
        amendement.setIsEditable(proposition.getIsEditable() && amendement.getUser().getId() == currentUser.getId());
        amendement.setIsVoteable(proposition.getIsVoteable());

        return amendement;
    }

    /**
     * Permet de récupérer l'ensemble des amendements d'une proposition
     *
     * @param projectId       id du projet
     * @param propositionId   id de la proposition
     * @return les amendements
     */
    public List<Amendement> getProjectPropositionAmendements(int projectId, int propositionId)
    {
        // Récupération de l'utilisateur qui veut accéder aux amendements
        User currentUser = Common.GetCurrentUser();

        // Récupération des amendements
        List<Amendement> propositionAmendements = amendementRepo.getProjectPropositionAmendements(projectId, propositionId);

        if(propositionAmendements != null && propositionAmendements.stream().findFirst().isPresent()){

            // Récupération de la proposition concernée
            Proposition proposition = propositionService.getProjectPropositionById(projectId, propositionId);
            boolean propositionIsEditable = proposition.getIsEditable();
            boolean propositionIsVoteable = proposition.getIsVoteable();

            // Permet de savoir si l'amendement peut être voté et modifié pour l'utilisateur connecté
            propositionAmendements.stream().forEach( a -> {
                a.setIsEditable(propositionIsEditable && a.getUser().getId() == currentUser.getId());
                a.setIsVoteable(propositionIsVoteable);
            });
        }

        return propositionAmendements;
    }

    /**
     * Permet d'amender une proposition
     *
     * @param projectId       id du projet
     * @param propositionId   id de la proposition
     * @param body            objet de l'amendement
     * @return l'amendement
     */
    public Amendement createProjectPropositionAmendement(int projectId, int propositionId, AmendementBody body)
    {
        // Récupération de l'utilisateur qui veut amender la proposition
        User currentUser = Common.GetCurrentUser();

        // Variables utilisées pour la date de création de l'amendement
        LocalDateTime localDateTime = LocalDateTime.now();
        Date now = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        // Récupération de la proposition à amender
        Proposition amendProposition = propositionService.getProjectPropositionById(projectId, propositionId);

        // Vérification que le user soit dans les gestionnaires ou une team de la proposition
        if(!amendProposition.getIsEditable())
            throw new CustomException("You do not have the right to amend this proposal", HttpStatus.FORBIDDEN);

        Amendement amend = new Amendement();
        amend.setTitle(body.getTitle());
        amend.setContent(body.getContent());
        amend.setAmendProposition(amendProposition);
        amend.setUser(currentUser);

        Amendement amendement = amendementRepo.save(amend);

        return getProjectPropositionAmendementById(projectId, propositionId, amendement.getId());
    }

    /**
     * Permet de modifier un amendement
     *
     * @param projectId       id du projet
     * @param propositionId   id de la proposition
     * @param amendementId      id de l'amendement
     * @param amendementBody  objet de l'amendement
     * @return l'amendement mis à jour
     */
    public Amendement updateProjectPropositionAmendementById(int projectId, int propositionId, int amendementId, AmendementBody amendementBody)
    {

        // Utilisateur qui demande à modifier la proposition
        User currentUser = Common.GetCurrentUser();

        // Récupération de l'amendement et génération d'exception si il ou le projet/proposition n'existe pas
        Amendement oldAmendement = getProjectPropositionAmendementById(projectId, propositionId, amendementId);

        // Vérification que l'utilisateur puisse modifier l'amendement
        if(!oldAmendement.getIsEditable())
            throw new CustomException("You do not have the right to modify this proposal amendement", HttpStatus.FORBIDDEN);

        // Modification des propriétés dans l'objet
        oldAmendement.setTitle(amendementBody.getTitle());
        oldAmendement.setContent(amendementBody.getContent());

        // Modification des champs en base de données
        amendementRepo.save(oldAmendement);

        // Retourne la proposition modifiée
        return getProjectPropositionAmendementById(projectId, propositionId, amendementId);
    }

    /**
     * Permet de supprimer un amendement par son utilisateur seulement
     *
     * @param projectId       id du projet
     * @param propositionId   id de la proposition
     * @param amendementId    id de l'amendement
     */
    public void deleteProjectPropositionAmendementById(int projectId, int propositionId, int amendementId)
    {

        // Récupération de l'amendement et génération d'exception si il ou le projet/proposition n'existe pas
        Amendement oldAmendement = getProjectPropositionAmendementById(projectId, propositionId, amendementId);


        // Vérification que l'utilisateur puisse supprimer l'amendement
        if(!oldAmendement.getIsEditable())
            throw new CustomException("You do not have the right to delete this proposal amendement", HttpStatus.FORBIDDEN);

        amendementRepo.deleteAmendementById(amendementId);
    }

    // =======================
    // === Amendement vote ===
    // =======================

    /**
     * Permet de récupérer les votes d'un amendement
     *
     * @param projectId     id du projet
     * @param propositionId id de la proposition
     * @param amendementId  id de l'amendement
     * @return la liste des votes de l'amendement
     */
    public List<PropositionVote> getProjectPropositionAmendementVotesById(int projectId, int propositionId, int amendementId)
    {
        // Permet de récupérer l'amendement et générer une erreur si existe pas
        Amendement amendement = getProjectPropositionAmendementById(projectId, propositionId, amendementId);

        return propositionVoteRepo.getProjectPropositionAmendementVotesById(projectId, propositionId, amendementId);
    }

    /**
     * Permet de voter un amendement si l'utilisateur a le droit et que c'est dans les délais de la proposition
     *
     * @param projectId     id du projet
     * @param propositionId id de la proposition
     * @param amendementId  id de l'amendement
     * @param body          vote de l'utilisateur
     * @return les votes de l'amendement
     */
    public List<PropositionVote> voteProjectPropositionAmendement(int projectId, int propositionId, int amendementId, PropositionVoteBody body)
    {
        // Récupération de l'utilisateur qui veut voter l'amendement
        User currentUser = Common.GetCurrentUser();

        // Permet de récupérer l'amendement et générer une erreur si il n'existe pas
        Amendement amendement = getProjectPropositionAmendementById(projectId, propositionId, amendementId);

        // On vérifie que l'amendement soit en période de vote et accessible par l'utilisateur
        if(!amendement.getIsVoteable())
            throw new CustomException("You do not have the right to vote this amendement", HttpStatus.FORBIDDEN);

        // On récupère la liste des votes déjà existant
        List<PropositionVote> amendementVotes = propositionVoteRepo.getProjectPropositionAmendementVotesById(projectId, propositionId, amendementId);

        // On vérifie que l'utilisateur n'ai pas déjà voté pour cet amendement
        if(amendementVotes != null && amendementVotes.stream().anyMatch(v -> v.getUser().getId() == currentUser.getId()))
            throw new CustomException("You have already voted for this amendement", HttpStatus.FORBIDDEN);

        // Si tout est bon alors on ajoute son vote
        propositionVoteRepo.createProjectPropositionAmendementVote(currentUser.getId(), propositionId, amendementId, body.getVoteType().getId());

        return propositionVoteRepo.getProjectPropositionAmendementVotesById(projectId, propositionId, amendementId);
    }
}
