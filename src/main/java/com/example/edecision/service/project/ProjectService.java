package com.example.edecision.service.project;

import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.project.Project;
import com.example.edecision.model.project.ProjectBody;
import com.example.edecision.model.project.ProjectStatus;
import com.example.edecision.model.project.ProjectUser;
import com.example.edecision.model.user.User;
import com.example.edecision.repository.project.ProjectRepository;
import com.example.edecision.repository.project.ProjectStatusRepository;
import com.example.edecision.repository.project.ProjectUserRepository;
import com.example.edecision.repository.team.TeamRepository;
import com.example.edecision.model.team.Team;
import com.example.edecision.repository.user.UserRepository;
import com.example.edecision.service.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    public ProjectRepository projectRepository;

    @Autowired
    public ProjectStatusRepository projectStatusRepository;

    @Autowired
    public TeamRepository teamRepository;

    @Autowired
    public ProjectUserRepository projectUserRepository;

    @Autowired
    public TeamService teamService;

    @Autowired
    public UserRepository userRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project createProject(ProjectBody projectBody) {
        ProjectStatus projectStatus = projectStatusRepository.findById(projectBody.project.getProject_status().getId()).get();
        projectBody.project.setProject_status(projectStatus);
        Project createdProject = projectRepository.save(projectBody.project);
        List<Team> freeTeams = getFreeTeams(projectBody.teams);
        verifyIfUsersExistsInTeams(List.of(projectBody.project_users), freeTeams);

        projectRepository.addProjectToTeam(projectBody.teams, projectBody.project.getId());
        addProjectUsersToProject(createdProject.getId(), projectBody.project_users);
        return createdProject;
    }

    private List<Team> getFreeTeams(int[] teamsId) {
        List<Team> freeTeams = teamService.getFreeTeamsWithUsers(teamsId);
        if (teamsId.length > freeTeams.size()) {
            throw new CustomException("You have provided one or more team already associated to a project", HttpStatus.BAD_REQUEST);
        }
        return freeTeams;
    }

    private void addProjectUsersToProject(int projectId, ProjectUser[] projectUsers) {
        for (ProjectUser projectUser : projectUsers) {
            projectUser.project_id = projectId;
            projectUserRepository.save(projectUser);
        }
    }

    private void verifyIfUsersExistsInTeams(List<ProjectUser> projectUsers, List<Team> freeTeams) {
        for (ProjectUser projectUser : projectUsers) {
            if (!isUserIsAtLeastInOneTeam(projectUser, freeTeams)) {
                throw new CustomException("User with id : " + projectUser.getUser_id() + " is not in any team that you have provided", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private boolean isUserIsAtLeastInOneTeam(ProjectUser projectUser, List<Team> freeTeams) {
        User user = userRepository.getById(projectUser.getUser_id());
        for (Team freeTeam : freeTeams) {
            if (freeTeam.getUsers().contains(user)) {
                return true;
            }
        }
        return false;
    }
}
