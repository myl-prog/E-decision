package com.example.edecision.controller;
import com.example.edecision.model.Proposition;
import com.example.edecision.model.User;
import com.example.edecision.service.PropositionService;
import com.example.edecision.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropositionController {
    @Autowired
    public PropositionService service;
    @GetMapping("/propositions")
    public List<Proposition> getAll() {
        return service.getAll();
    }
}
