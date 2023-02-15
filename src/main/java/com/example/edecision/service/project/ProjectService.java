package com.example.edecision.service.project;

import com.example.edecision.model.project.Project;
import com.example.edecision.model.project.ProjectBody;
import com.example.edecision.model.project.ProjectStatus;
import com.example.edecision.model.project.ProjectUser;
import com.example.edecision.repository.project.ProjectRepository;
import com.example.edecision.repository.project.ProjectStatusRepository;
import com.example.edecision.repository.project.ProjectUserRepository;
import com.example.edecision.repository.team.TeamRepository;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project createProject(ProjectBody project) {
        ProjectStatus projectStatus = projectStatusRepository.findById(project.project.getProject_status().getId()).get();
        project.project.setProject_status(projectStatus);
        Project createdProject = projectRepository.save(project.project);
        addProjectIdToTeam(project.teams, createdProject.getId());
        addProjectUsersToProject(createdProject.getId(), project.project_users);
        return createdProject;
    }

    private void addProjectIdToTeam(int[] teamsId, int projectId) {
        String concatTeamsIds = Arrays.toString(teamsId).replace("[", "(").replace("]", ")");
        System.out.println("ids : " + concatTeamsIds);
        projectRepository.addProjectToTeam(teamsId, projectId);
    }

    private void addProjectUsersToProject(int projectId, ProjectUser[] projectUsers) {
        for (ProjectUser projectUser : projectUsers) {
            projectUser.project_id = projectId;
            projectUserRepository.save(projectUser);
        }
    }
}
