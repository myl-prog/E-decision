package com.example.edecision.service.project;

import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.project.Project;
import com.example.edecision.model.project.ProjectBody;
import com.example.edecision.model.project.ProjectStatus;
import com.example.edecision.model.project.ProjectUser;
import com.example.edecision.model.user.User;
import com.example.edecision.model.user.UserRoleBody;
import com.example.edecision.repository.project.ProjectRepository;
import com.example.edecision.repository.project.ProjectStatusRepository;
import com.example.edecision.repository.project.ProjectUserRepository;
import com.example.edecision.repository.team.TeamRepository;
import com.example.edecision.model.team.Team;
import com.example.edecision.repository.user.UserRepository;
import com.example.edecision.service.team.TeamService;
import com.example.edecision.utils.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public Project getProjectById(int projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new CustomException("Project not found with id : " + projectId, HttpStatus.NOT_FOUND));
    }

    public Project createProject(ProjectBody projectBody) {
        if (projectStatusRepository.findById(projectBody.project.getProject_status().getId()).isPresent()) {
            ProjectStatus projectStatus = projectStatusRepository.findById(projectBody.project.getProject_status().getId()).get();
            projectBody.project.setProject_status(projectStatus);
            List<Team> freeTeams = getFreeTeams(projectBody.teams);
            verifyIfUsersExistsInTeams(projectBody.project_users, freeTeams);

            Project createdProject = projectRepository.save(projectBody.project);
            projectRepository.addProjectToTeams(projectBody.teams, projectBody.project.getId());
            addProjectUsersToProject(createdProject.getId(), projectBody.project_users);
            return createdProject;
        }
        throw new CustomException("This project status does not exist", HttpStatus.BAD_REQUEST);
    }

    public Project updateProject(int projectId, ProjectBody projectBody) {
        if (projectRepository.findById(projectId).isPresent()) {
            Project projectUpdated = projectRepository.findById(projectId).get();
            projectUpdated.setTitle(projectBody.project.getTitle());
            projectUpdated.setDescription(projectBody.project.getDescription());
            projectUpdated.setProject_status(projectBody.project.getProject_status());

            verificationsOnTeamsDuringProjectUpdate(projectId, projectBody);
            verificationsOnUsersDuringProjectUpdate(projectBody);

            List<Team> oldProjectTeamList = teamRepository.getTeamsByProject(projectId);
            List<ProjectUser> oldProjectUserList = projectUserRepository.getAllProjectUserByProject(projectId);
            modifyAssociatedTeams(projectId, projectBody, oldProjectTeamList);
            modifyProjectUsers(projectBody, oldProjectUserList);

            return projectRepository.save(projectUpdated);
        }
        throw new CustomException("Project not found with id : " + projectId, HttpStatus.NOT_FOUND);
    }

    public void deleteProject(int projectId) {
        if (projectRepository.findById(projectId).isPresent()) {
            verifyProjectOwnership(projectId);
            projectUserRepository.deleteAllProjectUserByProjectId(projectId);
            teamRepository.removeProjectIdFromTeams(projectId);
            projectRepository.deleteById(projectId);
        } else {
            throw new CustomException("Project not found with id : " + projectId, HttpStatus.NOT_FOUND);
        }
    }

    public ProjectUser changeUserRole(int projectId, int userId, UserRoleBody userRoleBody) {
        if (projectRepository.findById(projectId).isEmpty()) {
            throw new CustomException("Project not found with id : " + projectId, HttpStatus.NOT_FOUND);
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new CustomException("User not found with id : " + userId, HttpStatus.NOT_FOUND);
        }
        if (projectUserRepository.findProjectUserByProjectIdAndUserId(projectId, userId).isEmpty()) {
            throw new CustomException("This user is not associated to this project", HttpStatus.BAD_REQUEST);
        } else {
            ProjectUser projectUserUpdated = projectUserRepository.findProjectUserByProjectIdAndUserId(projectId, userId).get();
            projectUserUpdated.setUser_role_id(userRoleBody.userRoleId);
            return projectUserRepository.save(projectUserUpdated);
        }
    }

    private void verifyProjectOwnership(int projectId) {
        User projectOwner = userRepository.getProjectOwner(projectId);
        if (Common.GetCurrentUser().getId() != projectOwner.getId()) {
            throw new CustomException("You are not project owner, you can't perform this action", HttpStatus.UNAUTHORIZED);
        }
    }

    private void verificationsOnUsersDuringProjectUpdate(ProjectBody projectBody) {
        projectBody.project_users.forEach(projectUser -> {
            if (userRepository.findById(projectUser.getUser_id()).isEmpty()) {
                throw new CustomException("User with id : " + projectUser.getUser_id() + " doesn't exists", HttpStatus.BAD_REQUEST);
            } else {
                List<Team> teamList = new ArrayList<>();
                projectBody.teams.forEach(teamId -> {
                    Team team = teamRepository.getById(teamId);
                    team.setUsers(userRepository.findAllUsersByTeamId(teamId));
                    teamList.add(team);
                });
                if (!isUserIsAtLeastInOneTeam(projectUser, teamList)) {
                    throw new CustomException("User with id : " + projectUser.getUser_id() + " is not in any team that you have provided", HttpStatus.BAD_REQUEST);
                }
            }
        });
    }

    private void verificationsOnTeamsDuringProjectUpdate(int projectId, ProjectBody projectBody) {
        projectBody.teams.forEach(teamId -> {
            if (teamRepository.findById(teamId).isEmpty()) {
                throw new CustomException("Team with id : " + teamId + " doesn't exist", HttpStatus.BAD_REQUEST);
            } else {
                Team team = teamRepository.findById(teamId).get();
                if (team.getProject_id() != null && team.getProject_id() != projectId) {
                    throw new CustomException("Team with id : " + teamId + " is already associated to a project", HttpStatus.CONFLICT);
                }
            }
        });
    }

    private List<Team> getFreeTeams(List<Integer> teamsId) {
        List<Team> freeTeams = teamService.getFreeTeamsWithUsers(teamsId);
        if (teamsId.size() > freeTeams.size()) {
            throw new CustomException("You have provided one or more team already associated to a project", HttpStatus.BAD_REQUEST);
        }
        return freeTeams;
    }

    private void addProjectUsersToProject(int projectId, List<ProjectUser> projectUsers) {
        for (ProjectUser projectUser : projectUsers) {
            projectUser.project_id = projectId;
            projectUserRepository.save(projectUser);
        }
    }

    private void verifyIfUsersExistsInTeams(List<ProjectUser> projectUsers, List<Team> freeTeams) {
        projectUsers.forEach(projectUser -> {
            if (!isUserIsAtLeastInOneTeam(projectUser, freeTeams)) {
                throw new CustomException("User with id : " + projectUser.getUser_id() + " is not in any team that you have provided", HttpStatus.BAD_REQUEST);
            }
        });
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

    private void modifyProjectUsers(ProjectBody projectBody, List<ProjectUser> oldProjectUserList) {
        projectBody.project_users.forEach(projectUser -> {
            if (oldProjectUserList.stream().noneMatch(oldProjectUser -> projectUser.getUser_id() == oldProjectUser.getUser_id())) {
                projectUserRepository.save(projectUser);
            }
        });

        oldProjectUserList.forEach(oldProjectUser -> {
            if (projectBody.project_users.stream().noneMatch(projectUser -> oldProjectUser.getUser_id() == projectUser.getUser_id())) {
                projectUserRepository.delete(oldProjectUser);
            }
        });
    }

    private void modifyAssociatedTeams(int projectId, ProjectBody projectBody, List<Team> oldProjectTeamList) {
        projectBody.teams.forEach(teamId -> {
            if (oldProjectTeamList.stream().noneMatch(team -> teamId == team.getId())) {
                projectRepository.addProjectToTeam(teamId, projectId);
            }
        });

        oldProjectTeamList.forEach(team -> {
            if (!projectBody.teams.contains(team.getId())) {
                teamRepository.removeProjectIdFromTeam(projectId, team.getId());
            }
        });
    }
}
