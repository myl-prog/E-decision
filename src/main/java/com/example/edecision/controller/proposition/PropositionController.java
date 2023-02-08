package com.example.edecision.controller.proposition;
import com.example.edecision.model.proposition.AmendPropositionBody;
import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.model.proposition.PropositionBody;
import com.example.edecision.service.proposition.PropositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropositionController {
    @Autowired
    public PropositionService service;

    // ============================
    // ======= PROPOSITIONS =======
    // ============================

    @GetMapping("/propositions")
    public ResponseEntity<List<Proposition>> getAll() {
        try{
            return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
        }
        catch(Exception e){
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

    @PutMapping("/propositions")
    public ResponseEntity<Proposition> create(@RequestBody PropositionBody proposition) {
        try{
            return new ResponseEntity<>(service.create(proposition), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/propositions/{id}/amend")
    public ResponseEntity<Proposition> create(@PathVariable("id") int id, @RequestBody AmendPropositionBody proposition) {
        try{
            return new ResponseEntity<>(service.amend(id, proposition), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/propositions/{id}")
    public ResponseEntity<Proposition> update(@PathVariable("id") int id, @RequestBody PropositionBody proposition) {
        try{
            return new ResponseEntity<>(service.update(id, proposition), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/propositions/{id}")
    public ResponseEntity<HttpStatus> deletePropositionById(@PathVariable("id") int id) {
        try {
            service.deleteProposition(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ========================
    // ======= COMMENTS =======
    // ========================

    // =====================
    // ======= VOTES =======
    // =====================


}
