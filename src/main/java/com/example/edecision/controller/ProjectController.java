package com.example.edecision.controller;

import com.example.edecision.model.amendement.Amendement;
import com.example.edecision.model.comment.Comment;
import com.example.edecision.model.comment.CommentBody;
import com.example.edecision.model.project.Project;
import com.example.edecision.model.project.ProjectBody;
import com.example.edecision.model.project.ProjectUser;
import com.example.edecision.model.amendement.AmendementBody;
import com.example.edecision.model.proposition.*;
import com.example.edecision.model.team.Team;
import com.example.edecision.model.user.User;
import com.example.edecision.model.user.UserRoleBody;
import com.example.edecision.model.vote.JudgeVoteResult;
import com.example.edecision.model.vote.PropositionVote;
import com.example.edecision.service.AmendementService;
import com.example.edecision.service.CommentService;
import com.example.edecision.service.ProjectService;
import com.example.edecision.service.PropositionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {
    @Autowired
    public ProjectService projectService;

    @Autowired
    public PropositionService propositionService;

    @Autowired
    public AmendementService amendementService;

    @Autowired
    public CommentService commentService;

    // ===============
    // === Project ===
    // ===============

    @GetMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Récupère l'ensemble des projets" )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Les projets sont bien retournés", response = Project.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur")
    })
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getAllProjects());
    }

    @GetMapping(value = "/projects/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Récupère un projet avec son identifiant",
            notes = "Utilisable pour visualiser un projet")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le projet est bien retourné", response = Project.class),
            @ApiResponse(code = 404, message = "Le projet n'existe pas"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur")
    })
    public ResponseEntity<Project> getProjectById(@PathVariable("id") int id) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getProjectById(id));
    }

    @PostMapping(value = "/projects", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Créé un projet")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Le projet a bien été créé et est retourné", response = Project.class),
            @ApiResponse(code = 400, message = "Le statut du projet n'existe pas ou un utilisateur renseigné n'est pas dans une équipe du projet"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur")
    })
    public ResponseEntity<Project> createProject(@RequestBody ProjectBody projectBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectBody));
    }

    @PutMapping(value = "/projects/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modifie un projet")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le projet a bien été modifié et est retourné", response = Project.class),
            @ApiResponse(code = 400, message = "Le statut du projet, un utilisateur ou une équipe n'existe pas"),
            @ApiResponse(code = 404, message = "Le projet n'existe pas"),
            @ApiResponse(code = 409, message = "Une ou plusieurs équipes sont déjà rattachées à un projet"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur")
    })
    public ResponseEntity<Project> updateProject(@PathVariable("id") int id, @RequestBody ProjectBody projectBody) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.updateProject(id, projectBody));
    }

    @DeleteMapping(value = "/projects/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Supprime un projet")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le projet a bien été supprimé"),
            @ApiResponse(code = 404, message = "Le projet n'existe pas"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur")
    })
    public ResponseEntity<HttpStatus> deleteProject(@PathVariable("id") int id) {
        projectService.deleteProject(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ======================
    // ==== Project user ====
    // ======================

    @PatchMapping(value = "/projects/{projectId}/users/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modifie le rôle d'un utilisateur au sein d'un projet")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le rôle a bien été modifié et est retourné", response = ProjectUser.class),
            @ApiResponse(code = 400, message = "Le projet ou l'utilisateur n'existe pas"),
            @ApiResponse(code = 404, message = "L'utilisateur n'est pas associé à ce projet"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur")
    })
    public ResponseEntity<ProjectUser> changeUserRole(@PathVariable("projectId") int projectId, @PathVariable("userId") int userId, @RequestBody UserRoleBody userRoleBody) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.changeUserRole(projectId, userId, userRoleBody));
    }

    // ===========================
    // === Project proposition ===
    // ===========================

    @GetMapping(value = "/projects/{projectId}/propositions/{propositionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Récupère une proposition avec son identifiant",
            notes = "Utilisable pour visualiser une proposition")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La proposition est bien retournée", response = Proposition.class),
            @ApiResponse(code = 404, message = "La proposition n'existe pas ou n'est pas rattachée à ce projet"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur")
    })
    public ResponseEntity<Proposition> getProjectPropositionById(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId) {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.getProjectPropositionById(projectId, propositionId));
    }

    @PostMapping(value = "/projects/{projectId}/propositions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Créé une proposition",
            notes = "La date de fin doit être dans minimum une semaine et maximum 1 mois et l'utilisateur ne doit pas être compris dans une proposition énoncée il y a moins d'une semaine")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "La proposition a bien été créée et est retournée", response = Proposition.class),
            @ApiResponse(code = 400, message = "Le projet n'existe pas ou les délais de la date de fin ne sont pas respectés"),
            @ApiResponse(code = 403, message = "L'utilisateur est déjà dans une proposition trop récente"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur")
    })
    public ResponseEntity<Proposition> createProjectProposition(@PathVariable("projectId") int projectId, @RequestBody PropositionBody propositionBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propositionService.createProjectProposition(projectId, propositionBody));
    }

    @PutMapping(value = "/projects/{projectId}/propositions/{propositionId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modifie une proposition",
            notes = "L'utilisateur doit faire parti des gestionnaires et la proposition doit être dans les délais d'amendement")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La proposition a bien été modifiée et est retournée", response = Proposition.class),
            @ApiResponse(code = 403, message = "L'utilisateur n'a pas le droit de modifier cette proposition"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur"),
    })
    public ResponseEntity<Proposition> updateProjectProposition(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @RequestBody PropositionBody propositionBody) {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.updateProjectPropositionById(projectId, propositionId, propositionBody));
    }

    @PostMapping("/projects/{projectId}/propositions/{propositionId}/escalate")
    public ResponseEntity<EscalatePropositionResult> escalateProjectProposition(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId) {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.escalateProjectProposition(projectId, propositionId));
    }

    @DeleteMapping("/projects/{projectId}/propositions/{propositionId}")
    public ResponseEntity<DeletePropositionResult> deleteProposition(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId) {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.deleteProjectProposition(projectId, propositionId));
    }

    // ================================
    // === Project proposition vote ===
    // ================================

    @GetMapping("/projects/{projectId}/propositions/{propositionId}/votes")
    public ResponseEntity<List<PropositionVote>> getProjectPropositionVotes(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId) {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.getProjectPropositionVotesById(projectId, propositionId));
    }

    @PostMapping("/projects/{projectId}/propositions/{propositionId}/votes")
    public ResponseEntity<List<PropositionVote>> voteProjectProposition(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @RequestBody PropositionVoteBody voteBody) {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.voteProjectProposition(projectId, propositionId, voteBody));
    }

    @PostMapping("/projects/{projectId}/propositions/{propositionId}/judge")
    public ResponseEntity<JudgeVoteResult> judgeProjectProposition(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId) {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.judgeProjectProposition(projectId, propositionId));
    }

    // ===================================
    // === Project proposition comment ===
    // ===================================

    @GetMapping("/projects/{projectId}/propositions/{propositionId}/comments")
    public ResponseEntity<List<Comment>> getProjectPropositionComments(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getProjectPropositionCommentsById(projectId, propositionId));
    }

    @PostMapping("/projects/{projectId}/propositions/{propositionId}/comments")
    public ResponseEntity<Comment> createProjectPropositionComment(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @RequestBody CommentBody commentBody) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.createProjectPropositionComment(projectId, propositionId, commentBody));
    }

    @PutMapping("/projects/{projectId}/propositions/{propositionId}/comments/{commentId}")
    public ResponseEntity<Comment> updateProjectPropositionComment(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @PathVariable("commentId") int commentId, @RequestBody CommentBody commentBody) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.updateProjectPropositionComment(projectId, propositionId, commentId, commentBody));
    }

    @DeleteMapping("/projects/{projectId}/propositions/{propositionId}/comments/{commentId}")
    public ResponseEntity<HttpStatus> getProjectPropositionComments(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @PathVariable("commentId") int commentId) {
        commentService.deleteProjectPropositionCommentById(projectId, propositionId, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // =======================================
    // === Project proposition amendements ===
    // =======================================

    @GetMapping("/projects/{projectId}/propositions/{propositionId}/amendements")
    public ResponseEntity<List<Amendement>> getProjectPropositionAmendements(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId) {
        return ResponseEntity.status(HttpStatus.OK).body(amendementService.getProjectPropositionAmendements(projectId, propositionId));
    }

    @GetMapping("/projects/{projectId}/propositions/{propositionId}/amendements/{amendementId}")
    public ResponseEntity<Amendement> getProjectPropositionAmendement(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @PathVariable("amendementId") int amendementId) {
        return ResponseEntity.status(HttpStatus.OK).body(amendementService.getProjectPropositionAmendementById(projectId, propositionId, amendementId));
    }

    @PostMapping("/projects/{projectId}/propositions/{propositionId}/amendements")
    public ResponseEntity<Amendement> createProjectPropositionAmendement(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @RequestBody AmendementBody amendBody) {
        return ResponseEntity.status(HttpStatus.OK).body(amendementService.createProjectPropositionAmendement(projectId, propositionId, amendBody));
    }

    @PutMapping("/projects/{projectId}/propositions/{propositionId}/amendements/{amendementId}")
    public ResponseEntity<Amendement> updateProjectPropositionAmendement(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @PathVariable("amendementId") int amendementId, @RequestBody AmendementBody amendBody) {
        return ResponseEntity.status(HttpStatus.OK).body(amendementService.updateProjectPropositionAmendementById(projectId, propositionId, amendementId, amendBody));
    }

    @DeleteMapping("/projects/{projectId}/propositions/{propositionId}/amendements/{amendementId}")
    public ResponseEntity<HttpStatus> updateProjectPropositionAmendement(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @PathVariable("amendementId") int amendementId) {
        amendementService.deleteProjectPropositionAmendementById(projectId, propositionId, amendementId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ===========================================
    // === Project proposition amendement vote ===
    // ===========================================

    @GetMapping("/projects/{projectId}/propositions/{propositionId}/amendements/{amendementId}/votes")
    public ResponseEntity<List<PropositionVote>> getProjectPropositionVotes(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @PathVariable("amendementId") int amendementId) {
        return ResponseEntity.status(HttpStatus.OK).body(amendementService.getProjectPropositionAmendementVotesById(projectId, propositionId, amendementId));
    }

    @PostMapping("/projects/{projectId}/propositions/{propositionId}/amendements/{amendementId}/votes")
    public ResponseEntity<List<PropositionVote>> voteProjectProposition(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @PathVariable("amendementId") int amendementId, @RequestBody PropositionVoteBody voteBody) {
        return ResponseEntity.status(HttpStatus.OK).body(amendementService.voteProjectPropositionAmendement(projectId, propositionId, amendementId, voteBody));
    }

}
