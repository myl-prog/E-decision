package com.example.edecision.service.amendement;

import com.example.edecision.model.amendement.Amendement;
import com.example.edecision.model.amendement.AmendementBody;
import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.proposition.Proposition;
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

    public Amendement getProjectPropositionAmendementById(int projectId, int propositionId, int amendementId) {

        // Récupération de l'utilisateur qui veut accéder à la proposition
        User currentUser = Common.GetCurrentUser();

        // Récupération de l'amendement
        Optional<Amendement> optionalAmendement = amendementRepo.getProjectPropositionAmendementById(projectId, propositionId, amendementId);

        // Vérification que la proposition existe et qu'elle est visible pour ce user
        if(!optionalAmendement.isPresent())
            throw new CustomException("This amendement doesn't exists", HttpStatus.NOT_FOUND);

        Amendement amendement = optionalAmendement.get();
        Proposition proposition = propositionService.getProjectPropositionById(projectId, propositionId);

        // Permet de savoir, pour un utilisateur, si la proposition peut être modifiée et/ou votée
        amendement.setIsEditable(proposition.getEnd_time().getTime() > System.currentTimeMillis() && proposition.getProposition_status().getId() == 1 && amendement.getUser() == currentUser && propositionService.checkAmendDelay(proposition, false, true));
        amendement.setIsVoteable(proposition.getEnd_time().getTime() > System.currentTimeMillis() && proposition.getProposition_status().getId() == 1 && amendement.getUser() == currentUser && propositionService.checkAmendDelay(proposition, true, false));

        return amendement;
    }

    public Amendement createProjectPropositionAmendement(int projectId, int amendPropositionId, AmendementBody body)
    {
        // Récupération de l'utilisateur qui veut amender la proposition
        User currentUser = Common.GetCurrentUser();

        // Variables utilisées pour la date de création de l'amendement
        LocalDateTime localDateTime = LocalDateTime.now();
        Date now = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        // Récupération de la proposition à amender
        Proposition amendProposition = propositionService.getProjectPropositionById(projectId, amendPropositionId);

        // Vérification que le user soit dans les gestionnaires ou une team de la proposition
        if(!amendProposition.getIsEditable())
            throw new CustomException("You do not have the right to amend this proposal", HttpStatus.FORBIDDEN);

        Amendement amend = new Amendement();
        amend.setTitle(body.getTitle());
        amend.setContent(body.getContent());
        amend.setAmend_proposition(amendProposition);
        amend.setUser(currentUser);

        Amendement amendement = amendementRepo.save(amend);

        return getProjectPropositionAmendementById(projectId, amendPropositionId, amendement.getId());
    }
}
