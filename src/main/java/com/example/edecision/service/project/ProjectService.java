package com.example.edecision.service.project;
import com.example.edecision.model.project.Project;
import com.example.edecision.model.project.ProjectBody;
import com.example.edecision.model.project.ProjectStatus;
import com.example.edecision.model.proposition.PropositionStatus;
import com.example.edecision.model.team.Team;
import com.example.edecision.repository.project.ProjectRepository;
import com.example.edecision.repository.project.ProjectStatusRepository;
import com.example.edecision.repository.team.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Project> getAllProjects(){
        return projectRepository.findAll();
    }

    public Project createProject(ProjectBody project) {
        ProjectStatus projectStatus = projectStatusRepository.findById(project.project.getProjectStatus().getId()).get();
        project.project.setProjectStatus(projectStatus);
        Project createdProject = projectRepository.save(project.project);
        addProjectIdToTeam(project.projectTeamsId, createdProject.getId());
        return createdProject;
    }

    public void addProjectIdToTeam(int[] teamsId, int projectId) {
        for (int teamId : teamsId) {
            Team team = teamRepository.getById(teamId);

            // if (team.getProject_id() == null) {
            //    projectRepository.addProjectToTeam(projectId);
            //}
        }
    }
}
