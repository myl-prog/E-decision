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
    public ResponseEntity<List<Proposition>> getAll() {
        try{
            return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/propositions/{id}")
    public ResponseEntity<Proposition> getById(@PathVariable("id") Integer id) {
        try{
            return new ResponseEntity<>(service.getById(id), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/propositions")
    public ResponseEntity<Proposition> create(@RequestBody Proposition proposition) {
        try{
            return new ResponseEntity<>(service.create(proposition), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
