package com.example.edecision.service.proposition;

import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.project.Project;
import com.example.edecision.model.proposition.PropositionBody;
import com.example.edecision.repository.project.ProjectRepository;
import com.example.edecision.service.team.TeamService;
import com.example.edecision.service.user.UserService;
import com.example.edecision.utils.Common;
import com.example.edecision.model.team.Team;
import com.example.edecision.model.user.User;
import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.repository.proposition.PropositionRepository;
import com.example.edecision.repository.proposition.PropositionStatusRepository;
import com.example.edecision.repository.team.TeamRepository;
import com.example.edecision.repository.teamProposition.TeamPropositionRepository;
import com.example.edecision.repository.user.UserRepository;
import com.example.edecision.repository.userProposition.UserPropositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Autowired
    public UserService userService;

    @Autowired
    public TeamService teamService;

    @Autowired
    public ProjectRepository projectRepository;

    /**
     * Get all propositions where current user is associated
     *
     * @return The proposition list associated to current user
     */
    public List<Proposition> getAllPropositionsByUser() {
        User user = Common.GetCurrentUser();
        return propositionRepo.getPropositionsByUser(user.getId());
    }

    /**
     * Get a project proposition by id for current user
     *
     * @param projectId     projectId
     * @param propositionId propositionId
     * @return a proposition
     */
    public Proposition getProjectPropositionById(int projectId, int propositionId) {
        User user = Common.GetCurrentUser();
        Optional<Proposition> optionalProposition = propositionRepo.getProjectPropositionById(projectId, propositionId);
        if (optionalProposition.isPresent()) {
            Proposition proposition = optionalProposition.get();
            List<Team> projectTeams = teamService.getTeamsByProject(projectId);
            if (userService.isUserInTeams(user.getId(), projectTeams)) {
                List<Team> teamPropositionList = teamService.getTeamsByPropositions(propositionId);
                boolean isUserInTeamsProposition = userService.isUserInTeams(user.getId(), teamPropositionList);
                List<User> users = userRepo.getUsersByProposition(proposition.getId());
                proposition.setUsers(users);

                proposition.setIsEditable(proposition.getEnd_time().getTime() >= System.currentTimeMillis() && isUserInTeamsProposition);
                proposition.setIsVoteable(proposition.getEnd_time().getTime() < System.currentTimeMillis() && proposition.getProposition_status().getId() == 1 && isUserInTeamsProposition);
                return proposition;
            } else {
                throw new CustomException("You have not access to this proposition", HttpStatus.UNAUTHORIZED);
            }
        } else {
            throw new CustomException("This proposition doesn't exists", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Permit to create a project proposition
     *
     * @param projectId       projectId
     * @param propositionBody propositionBody
     * @return the created proposition
     */
    public Proposition createProposition(int projectId, PropositionBody propositionBody) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()) {
            if (propositionStatusRepo.findById(propositionBody.proposition.getProposition_status().getId()).isPresent()) {
                propositionBody.proposition.setProject(optionalProject.get());
                propositionBody.proposition.setProposition_status(propositionBody.proposition.getProposition_status());
                if (propositionBody.proposition.getAmendment_delay() <= 0) {
                    propositionBody.proposition.setAmendment_delay(1);
                }
                Proposition createdProposition = propositionRepo.save(propositionBody.proposition);
                createUsersProposition(propositionBody.users, createdProposition.getId());
                createTeamsProposition(propositionBody.teams, createdProposition.getId());
                return createdProposition;
            } else {
                throw new CustomException("This project status doesn't exists", HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new CustomException("This project doesn't exists", HttpStatus.BAD_REQUEST);
        }
    }

    /*public Proposition amend(int amendPropositionId, AmendPropositionBody body) {

        User user = Common.GetCurrentUser();
        Proposition amendProposition = propositionRepo.getProjectPropositionById(amendPropositionId, user.getId());

        if (amendProposition != null) {
            Proposition proposition = new Proposition();
            proposition.setTitle(body.getTitle());
            proposition.setContent(body.getContent());
            proposition.setAmend_proposition(amendProposition);
            proposition.setEnd_time(amendProposition.getEnd_time());
            return propositionRepo.save(proposition);
        } else {
            // ERROR
            return null;
        }

    }*/

    /*public Proposition update(int id, PropositionBody propositon) {

        // Todo : check user and check if we are in amandment delay, end time etc

        Proposition oldProposition = getProjectPropositionById(id);

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
            for (int userId : propositon.users) {
                if (oldUsers == null || !oldUsers.stream().anyMatch(x -> x.getId() == userId)) {
                    userPropositionRepo.save(new UserProposition(userId, id));
                }
            }
        }

        // users to delete in user_proposition
        if (oldUsers != null) {
            for (User user : oldUsers) {
                if (propositon.users == null || !(IntStream.of(propositon.users).anyMatch(x -> x == user.getId()))) {
                    userPropositionRepo.deleteUserProposition(id, user.getId());
                }
            }
        }

        // teams to create in team_proposition
        if (propositon.teams != null) {
            for (int teamId : propositon.teams) {
                if (oldTeams == null || !oldTeams.stream().anyMatch(x -> x.getId() == teamId)) {
                    teamPropositionRepo.save(new TeamProposition(teamId, id));
                }
            }
        }

        // teams to delete in team_proposition
        if (oldTeams != null) {
            for (Team team : oldTeams) {
                if (propositon.teams == null || !(IntStream.of(propositon.teams).anyMatch(x -> x == team.getId()))) {
                    teamPropositionRepo.deleteTeamProposition(id, team.getId());
                }
            }
        }

        return getProjectPropositionById(oldProposition.getId());
    }*/

    /**
     * Permit to delete a project proposition
     *
     * @param projectId     projectId
     * @param propositionId proposition id
     */
    public void deleteProposition(int projectId, int propositionId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()) {
            // Todo check user before delete proposition
            userPropositionRepo.deleteUserPropositionsByProposition(propositionId);
            teamPropositionRepo.deleteTeamPropositionsByProposition(propositionId);
            propositionRepo.deleteProposition(propositionId);
        } else {
            throw new CustomException("This project doesn't exists", HttpStatus.BAD_REQUEST);
        }
    }

    private void createUsersProposition(int[] users, int proposition) {
        for (int user : users) {
            userPropositionRepo.createUserProposition(proposition, user);
        }
    }

    private void createTeamsProposition(int[] teams, int proposition) {
        for (int team : teams) {
            teamPropositionRepo.createTeamProposition(proposition, team);
        }
    }

}
