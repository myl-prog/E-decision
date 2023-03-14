package com.example.edecision.controller;

import com.example.edecision.model.authentication.AuthResponse;
import com.example.edecision.model.project.ProjectStatus;
import com.example.edecision.model.proposition.PropositionStatus;
import com.example.edecision.model.team.TeamType;
import com.example.edecision.model.user.UserRole;
import com.example.edecision.model.vote.VoteType;
import com.example.edecision.service.ProjectService;
import com.example.edecision.service.PropositionService;
import com.example.edecision.service.TeamService;
import com.example.edecision.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SettingController {

    @Autowired
    public ProjectService projectService;
    @Autowired
    public PropositionService propositionService;
    @Autowired
    public TeamService teamService;
    @Autowired
    public UserService userService;

    @GetMapping(value = "/project-status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Permet de récupérer l'ensemble des statuts possibles pour un projet",
                  notes = "Retourne la liste des statuts, peut être utilisé pour la création d'un formulaire lié au projet")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Les statuts sont bien retournés", response = ProjectStatus.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur"),
    })
    public ResponseEntity<List<ProjectStatus>> getAllProjectStatus() {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getProjectStatus());
    }

    @GetMapping(value = "/proposition-status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Permet de récupérer l'ensemble des statuts possibles pour une proposition",
            notes = "Retourne la liste des statuts, peut être utilisé pour la création d'un formulaire lié à une proposition")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Les statuts sont bien retournés", response = PropositionStatus.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur"),
    })
    public ResponseEntity<List<PropositionStatus>> getAllPropositionStatus() {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.getPropositionStatus());
    }

    @GetMapping(value = "/team-types", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Permet de récupérer l'ensemble des différents types d'équipes possibles",
            notes = "Retourne la liste des types, peut être utilisé pour la création d'un formulaire lié à une équipe")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Les types sont bien retournés", response = TeamType.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur"),
    })
    public ResponseEntity<List<TeamType>> getAllTeamTypes() {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getTeamTypes());
    }

    @GetMapping(value = "/user-roles", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Permet de récupérer l'ensemble des différents rôles d'utilisateurs possibles",
            notes = "Retourne la liste des rôles, peut être utilisé pour la création d'un formulaire lié à un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Les types sont bien retournés", response = UserRole.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur"),
    })
    public ResponseEntity<List<UserRole>> getAllUserRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserRoles());
    }

    @GetMapping(value = "/vote-types", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Permet de récupérer l'ensemble des différents types de votes possibles",
            notes = "Retourne la liste des types, peut être utilisé pour la création d'un formulaire lié à aux votes")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Les types sont bien retournés", response = VoteType.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur"),
    })
    public ResponseEntity<List<VoteType>> getAllVoteTypes() {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.getVoteTypes());
    }

}
