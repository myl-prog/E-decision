package com.example.edecision.service.proposition;
import com.example.edecision.Utils.Common;
import com.example.edecision.model.proposition.AmendPropositionBody;
import com.example.edecision.model.team.Team;
import com.example.edecision.model.teamProposition.TeamProposition;
import com.example.edecision.model.user.User;
import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.model.proposition.PropositionBody;
import com.example.edecision.model.proposition.PropositionStatus;
import com.example.edecision.model.userProposition.UserProposition;
import com.example.edecision.repository.proposition.PropositionRepository;
import com.example.edecision.repository.proposition.PropositionStatusRepository;
import com.example.edecision.repository.team.TeamRepository;
import com.example.edecision.repository.teamProposition.TeamPropositionRepository;
import com.example.edecision.repository.user.UserRepository;
import com.example.edecision.repository.userProposition.UserPropositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    public UserRepository userRepo;

    @Autowired
    public TeamRepository teamRepo;

    // GET

    public List<Proposition> getAll(){

        // TODO : filter with token
        User user = Common.GetCurrentUser();

        return propositionRepo.getPropositionsByUser(user.getId());
    }

    public Proposition getById(Integer id) {

        Proposition proposition = propositionRepo.findById(id).get();

        // TODO : switch user/team id ?
        proposition.setIsEditable(proposition.getEnd_time().getTime() >= System.currentTimeMillis());
        proposition.setIsVoteable(proposition.getEnd_time().getTime() < System.currentTimeMillis()); // TODO && status != ...

        // return user proposition
        try{
            ArrayList<User> users = userRepo.getUsersByProposition(proposition.getId());
            proposition.setUsers(users);
        }
        catch(Exception e){
            proposition.setUsers(new ArrayList<User>());
        }

        // return team proposition
        try{
            ArrayList<Team> teams = teamRepo.getTeamsByProposition(proposition.getId());
            proposition.setTeams(teams);
        }
        catch(Exception e){
            proposition.setTeams(new ArrayList<Team>());
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

        List<Team> oldTeams = oldProposition.getTeams();
        List<User> oldUsers = oldProposition.getUsers();
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
                if(oldUsers == null || !oldUsers.stream().anyMatch(x -> x.getId() == userId)){
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
                if(oldTeams == null || !oldTeams.stream().anyMatch(x -> x.getId() == teamId)){
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
