package com.example.edecision.controller;

import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.model.team.Team;
import com.example.edecision.model.user.User;
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
public class PropositionController {
    @Autowired
    public PropositionService propositionService;

    // ===================
    // === Proposition ===
    // ===================

    @GetMapping(value = "/propositions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Récupère l'ensemble des propositions auxquels l'utilisateur est rattaché")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Les propositions sont bien retournées", response = Proposition.class, responseContainer = "List" ),
            @ApiResponse(code = 500, message = "Erreur interne du serveur"),
    })
    public ResponseEntity<List<Proposition>> getAllPropositionsByUser() {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.getAllPropositionsByUser());
    }
}
