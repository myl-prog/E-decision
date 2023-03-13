package com.example.edecision.controller;

import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.service.PropositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/propositions")
    public ResponseEntity<List<Proposition>> getAllPropositionsByUser() {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.getAllPropositionsByUser());
    }
}
