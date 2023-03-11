package com.example.edecision.service.amendement;

import com.example.edecision.model.amendement.Amendement;
import com.example.edecision.model.amendement.AmendementBody;
import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.model.proposition.PropositionBody;
import com.example.edecision.model.team.Team;
import com.example.edecision.model.user.User;
import com.example.edecision.model.userProposition.UserProposition;
import com.example.edecision.repository.amendement.AmendementRepository;
import com.example.edecision.service.project.ProjectService;
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
    public PropositionService propositionService;

    @Autowired
    public UserService userService;

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
            boolean propositionIsVoteable = proposition.getIsEditable();

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
        amend.setAmend_proposition(amendProposition);
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

        // Utilisateur qui demande à modifier la proposition
        User currentUser = Common.GetCurrentUser();

        // Récupération de l'amendement et génération d'exception si il ou le projet/proposition n'existe pas
        Amendement oldAmendement = getProjectPropositionAmendementById(projectId, propositionId, amendementId);


        // Vérification que l'utilisateur puisse supprimer l'amendement
        if(!oldAmendement.getIsEditable())
            throw new CustomException("You do not have the right to delete this proposal amendement", HttpStatus.FORBIDDEN);

        amendementRepo.deleteAmendementById(amendementId);
    }
}
