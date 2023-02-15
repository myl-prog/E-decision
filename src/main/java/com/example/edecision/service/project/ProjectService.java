package com.example.edecision.service.project;

import com.example.edecision.model.project.Project;
import com.example.edecision.model.project.ProjectBody;
import com.example.edecision.model.project.ProjectStatus;
import com.example.edecision.model.project.ProjectUser;
import com.example.edecision.repository.project.ProjectRepository;
import com.example.edecision.repository.project.ProjectStatusRepository;
import com.example.edecision.repository.project.ProjectUserRepository;
import com.example.edecision.repository.team.TeamRepository;
import com.example.edecision.repository.teamProposition.team.Team;
import com.example.edecision.service.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project createProject(ProjectBody project) {
        ProjectStatus projectStatus = projectStatusRepository.findById(project.project.getProject_status().getId()).get();
        project.project.setProject_status(projectStatus);
        Project createdProject = projectRepository.save(project.project);
        addProjectToFreeTeams(project.teams, project.project.getId());
        addProjectUsersToProject(createdProject.getId(), project.project_users);
        return createdProject;
    }

    private void addProjectToFreeTeams(int[] teamsId, int projectId) {
        List<Team> teams = teamService.getFreeTeamsWithUsers(teamsId);
        int[] ids = teams.stream().mapToInt(team -> team.getId()).toArray();
        projectRepository.addProjectToTeam(ids, projectId);
    }

    private void addProjectUsersToProject(int projectId, ProjectUser[] projectUsers) {
        for (ProjectUser projectUser : projectUsers) {
            projectUser.project_id = projectId;
            projectUserRepository.save(projectUser);
        }
    }
}
