package com.example.edecision.service;
import com.example.edecision.model.*;
import com.example.edecision.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    // GET

    public List<Proposition> getAll(){

        // TODO : filter with token
        return propositionRepo.findAll();
    }

    public Proposition getById(Integer id) {

        Proposition proposition = propositionRepo.findById(id).get();

        // TODO : switch user/team id ?
        proposition.setIsEditable(proposition.getEnd_time().getTime() >= System.currentTimeMillis());
        proposition.setIsVoteable(proposition.getEnd_time().getTime() < System.currentTimeMillis()); // TODO && status != ...

        // return user proposition
        try{
            List<Object[]> usersObject = userPropositionRepo.getUserPropositionByProposition(id);
            if(usersObject.size() > 0){
                User[] users = new User[usersObject.size()];
                for(int i = 0; i< usersObject.size(); i++){

                    Object[] array = Arrays.stream(usersObject.get(i)).toArray();
                    User u = new User((Integer) array[0], array[1].toString(), array[2].toString(), array[3].toString(), array[4].toString(), 0);
                    if (array[5] != null) u.setUser_role_id((Integer)array[5]);
                    users[i] = u;
                }
                proposition.setUsers(users);
            }
        }catch(Exception e){
            proposition.setUsers(null);
            System.out.println(e.getMessage());
        }

        // return team proposition
        try{
            List<Object[]> teamsObject = teamPropositionRepo.getTeamPropositionByProposition(id);
            if(teamsObject.size() > 0){
                Team[] teams = new Team[teamsObject.size()];
                for(int i = 0; i< teamsObject.size(); i++){

                    Object[] array = Arrays.stream(teamsObject.get(i)).toArray();
                    Team t = new Team((Integer) array[0], array[1].toString(), (Integer) array[2], (Integer) array[3]);
                    teams[i] = t;
                }
                proposition.setTeams(teams);
            }
        }catch(Exception e){
            proposition.setTeams(null);
            System.out.println(e.getMessage());
        }

        return proposition;
    }

    // CREATE

    public Proposition create(PropositionBody propositon){

        // get proposition status
        PropositionStatus status = propositionStatusRepo.findById(propositon.proposition.getProposition_status().getId()).get();
        propositon.proposition.setProposition_status(status);
        if(propositon.proposition.getAmendment_delay() <= 0) propositon.proposition.setAmendment_delay(1); // default amendment delay one day
        Proposition createdProposition = propositionRepo.save(propositon.proposition);

        // affect user and team to the proposition
        createUsersProposition(propositon.users, createdProposition.getId());
        createTeamsProposition(propositon.teams, createdProposition.getId());
        return getById(createdProposition.getId());
    }

    public void createUsersProposition(int[] users, int proposition){
        for (int user : users){
            userPropositionRepo.createUserProposition(proposition, user);
        }
    }

    public void createTeamsProposition(int[] teams, int proposition){
        for (int team : teams){
            teamPropositionRepo.createTeamProposition(proposition, team);
        }
    }

    public Proposition amend (int amendPropositionId, AmendPropositionBody body){
        // TODO : check authorization

        Proposition amendProposition = propositionRepo.findById(amendPropositionId).get();

        Proposition proposition = new Proposition();
        proposition.setTitle(body.getTitle());
        proposition.setContent(body.getContent());
        proposition.setAmend_proposition(amendProposition);
        proposition.setEnd_time(amendProposition.getEnd_time());
        return propositionRepo.save(proposition);
    }

    // UPDATE
    public Proposition update(int id, PropositionBody propositon){

        // Todo : check user and check if we are in amandment delay, end time etc

        Proposition oldProposition = getById(id);

        Team[] oldTeams = oldProposition.getTeams();
        User[] oldUsers = oldProposition.getUsers();
        oldProposition.setTeams(null);
        oldProposition.setUsers(null);

        // Save in proposition table
        oldProposition.setTitle(propositon.proposition.getTitle());
        oldProposition.setEnd_time(propositon.proposition.getEnd_time());
        oldProposition.setAmendment_delay(propositon.proposition.getAmendment_delay());
        oldProposition.setContent(propositon.proposition.getContent());
        oldProposition.setAmend_proposition(propositon.proposition.getAmend_proposition());

        propositionRepo.save(oldProposition);

        // users to create in user_proposition
        if (propositon.users != null) {
            for (int userId : propositon.users){
                if(oldUsers == null || !(Stream.of(oldUsers).anyMatch(x -> x.getId() == userId))){
                    userPropositionRepo.save(new UserProposition(userId, id));
                }
            }
        }

        // users to delete in user_proposition
        if(oldUsers != null){
            for(User user : oldUsers){
                if(propositon.users == null || !(IntStream.of(propositon.users).anyMatch(x -> x == user.getId()))){
                    userPropositionRepo.deleteUserProposition(id,user.getId());
                }
            }
        }

        // teams to create in team_proposition
        if (propositon.teams != null) {
            for (int teamId : propositon.teams){
                if(oldTeams == null || !(Stream.of(oldTeams).anyMatch(x -> x.getId() == teamId))){
                    teamPropositionRepo.save(new TeamProposition(teamId, id));
                }
            }
        }

        // teams to delete in team_proposition
        if(oldTeams != null){
            for(Team team : oldTeams){
                if(propositon.teams == null || !(IntStream.of(propositon.teams).anyMatch(x -> x == team.getId()))){
                    teamPropositionRepo.deleteTeamProposition(id,team.getId());
                }
            }
        }

        return getById(oldProposition.getId());
    }

    // DELETE

    public void deleteProposition(int id) {
        // Todo check user before delete proposition
        userPropositionRepo.deleteUserPropositionsByProposition(id);
        teamPropositionRepo.deleteTeamPropositionsByProposition(id);
        propositionRepo.deleteProposition(id);
    }

}
