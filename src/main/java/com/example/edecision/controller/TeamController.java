package com.example.edecision.controller;

import com.example.edecision.model.exception.ErrorMessage;
import com.example.edecision.model.project.ProjectStatus;
import com.example.edecision.model.team.Team;
import com.example.edecision.model.team.TeamBody;
import com.example.edecision.service.TeamService;
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
public class TeamController {
    @Autowired
    public TeamService teamService;

    // ============
    // === Team ===
    // ============

    @GetMapping(value = "/teams", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Récupère l'ensemble des équipes",
            notes = "Utilisable pour le formulaire de création et modification d'un projet ou d'une proposition")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Les équipes sont bien retournées", response = Team.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur", response = ErrorMessage.class),
    })
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getAllTeams());
    }

    @GetMapping(value = "/teams/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Récupère une équipe avec son identifiant",
            notes = "Utilisable pour visualiser une équipe")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'équipe est bien retournée", response = Team.class),
            @ApiResponse(code = 404, message = "L'équipe n'existe pas", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Erreur interne du serveur", response = ErrorMessage.class),
    })
    public ResponseEntity<Team> getById(@PathVariable("id") int id) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getTeamById(id));
    }

    @PostMapping(value = "/teams", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Créé une équipe",
            notes = "Aucune équipe avec le même nom ne doit déjà exister")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "L'équipe a bien été créée et est retournée", response = Team.class),
            @ApiResponse(code = 404, message = "Un ou plusieurs utilisateurs de l'équipe n'existent pas", response = ErrorMessage.class),
            @ApiResponse(code = 409, message = "Une équipe existe déjà avec le même nom", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Erreur interne du serveur", response = ErrorMessage.class),
    })
    public ResponseEntity<Team> createTeam(@RequestBody TeamBody teamBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(teamBody));
    }

    @PutMapping(value = "/teams/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modifie une équipe",
            notes = "Aucune équipe avec le même nom ne doit déjà exister")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'équipe a bien été modifiée et est retournée", response = Team.class),
            @ApiResponse(code = 404, message = "L'équipe n'existe pas", response = ErrorMessage.class),
            @ApiResponse(code = 409, message = "Une équipe existe déjà avec le même nom", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Erreur interne du serveur", response = ErrorMessage.class),
    })
    public ResponseEntity<Team> updateTeam(@PathVariable("id") int teamId, @RequestBody TeamBody teamBody) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.updateTeam(teamId, teamBody));
    }

    @DeleteMapping(value = "/teams/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Supprime une équipe")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'équipe a bien été supprimée"),
            @ApiResponse(code = 404, message = "L'équipe n'existe pas", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Erreur interne du serveur", response = ErrorMessage.class),
    })
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("id") int id) {
        teamService.deleteTeam(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
