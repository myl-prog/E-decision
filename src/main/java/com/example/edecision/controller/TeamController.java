package com.example.edecision.controller;

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

    @GetMapping(value = "/teams", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Récupère l'ensemble des équipes",
            notes = "Retourne la liste des équipes, peut être utilisé pour la création d'un formulaire lié au projet")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Les statuts sont bien retournés", response = ProjectStatus.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur"),
    })
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getAllTeams());
    }

    @GetMapping("/teams/{id}")
    public ResponseEntity<Team> getById(@PathVariable("id") int id) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getTeamById(id));
    }

    @PostMapping("/teams")
    public ResponseEntity<Team> createTeam(@RequestBody TeamBody teamBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(teamBody));
    }

    @PutMapping("/teams/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable("id") int teamId, @RequestBody TeamBody teamBody) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.updateTeam(teamId, teamBody));
    }

    @DeleteMapping("teams/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("id") int id) {
        teamService.deleteTeam(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
