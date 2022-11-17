package com.example.edecision.service;
import com.example.edecision.model.Proposition;
import com.example.edecision.model.User;
import com.example.edecision.repository.PropositionRepository;
import com.example.edecision.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropositionService {
    @Autowired
    public PropositionRepository repo;

    public List<Proposition> getAll(){
        return repo.findAll();
    }

    public Proposition getById(Integer id) {
        return repo.findById(id).get();
    }

    public Proposition create(Proposition propositon){
        return repo.save(propositon);
    }

}
