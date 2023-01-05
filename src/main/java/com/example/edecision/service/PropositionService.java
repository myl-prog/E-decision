package com.example.edecision.service;
import com.example.edecision.model.*;
import com.example.edecision.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropositionService {
    @Autowired
    public PropositionRepository propositionRepo;

    @Autowired
    public PropositionStatusRepository propositionStatusRepo;

    @Autowired
    public UserPropositionRepository userPropositionRepo;

    @Autowired
    public TeamPropositionRepository teamPropositionRepo;

    public List<Proposition> getAll(){
        return propositionRepo.findAll();
    }

    public Proposition getById(Integer id) {
        return propositionRepo.findById(id).get();
    }

    public Proposition create(PropositionBody propositon){

        PropositionStatus status = propositionStatusRepo.findById(propositon.proposition.getProposition_status().getId()).get();
        propositon.proposition.setProposition_status(status);
        Proposition createdProposition = propositionRepo.save(propositon.proposition);

        System.out.println(createdProposition);
        createUsersProposition(propositon.users, createdProposition.getId());
        createTeamsProposition(propositon.teams, createdProposition.getId());
        return createdProposition;
    }

    public void createUsersProposition(int[] users, int proposition){
        for (int user : users){
            UserProposition userProposition = new UserProposition();
            userProposition.user_id = user;
            userProposition.proposition_id = proposition;
            userPropositionRepo.save(userProposition);
        }
    }

    public void createTeamsProposition(int[] teams, int proposition){
        for (int team : teams){
            TeamProposition teamProposition = new TeamProposition();
            teamProposition.team_id = team;
            teamProposition.proposition_id = proposition;
            teamPropositionRepo.save(teamProposition);
        }
    }

}
