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
import com.example.edecision.service.user.UserService;
import com.example.edecision.utils.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public UserService userService;

    @Autowired
    public UserRepository userRepository;

    // ===============
    // === Project ===
    // ===============

    /**
     * Permet de récupérer tous les projets
     */
    public List<Project> getAllProjects() {
        List<Project> allProjects = projectRepository.findAll();
        allProjects.forEach(project -> {
            addTeamsToProject(project.getId());
        });
        return allProjects;
    }

    /**
     * Permet de récupérer un projet avec son identifiant
     *
     * @param projectId id du projet
     * @return le projet
     */
    public Project getProjectById(int projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            addTeamsToProject(projectId);
            return project;
        } else {
            throw new CustomException("This project doesn't exists", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Permet de créer le projet et de lui associer les équipes et les utilisateurs
     *
     * @param projectBody objet du projet avec ses équipes et utilisateurs/rôles
     * @return le projet créé
     */
    public Project createProject(ProjectBody projectBody) {
        if (projectStatusRepository.findById(projectBody.getProject().getProjectStatus().getId()).isPresent()) {
            ProjectStatus projectStatus = projectStatusRepository.findById(projectBody.getProject().getProjectStatus().getId()).get();
            projectBody.getProject().setProjectStatus(projectStatus);
            List<Team> freeTeams = getFreeTeams(projectBody.getTeamIdList());
            verifyIfUsersExistsInTeams(projectBody.getProjectUsers(), freeTeams);

            Project createdProject = projectRepository.save(projectBody.getProject());
            projectRepository.addProjectToTeams(projectBody.getTeamIdList(), projectBody.getProject().getId());
            addProjectUsersToProject(createdProject.getId(), projectBody.getProjectUsers());
            return getProjectById(createdProject.getId());
        } else {
            throw new CustomException("This project status does not exist", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Permet de mettre à jour le projet avec ses utilisateurs et ses équipes
     *
     * @param projectId   id du projet
     * @param projectBody objet du projet
     * @return le projet mis à jour
     */
    public Project updateProject(int projectId, ProjectBody projectBody) {
        if (projectRepository.findById(projectId).isPresent()) {
            if (projectStatusRepository.findById(projectBody.getProject().getProjectStatus().getId()).isEmpty()) {
                throw new CustomException("This project status does not exist", HttpStatus.BAD_REQUEST);
            } else {
                Project projectUpdated = projectRepository.findById(projectId).get();
                projectUpdated.setTitle(projectBody.getProject().getTitle());
                projectUpdated.setDescription(projectBody.getProject().getDescription());
                projectUpdated.setProjectStatus(projectBody.getProject().getProjectStatus());

                verificationsOnTeamsDuringProjectUpdate(projectId, projectBody);
                verificationsOnUsersDuringProjectUpdate(projectBody);

                List<Team> oldProjectTeamList = teamRepository.getTeamsByProject(projectId);
                List<ProjectUser> oldProjectUserList = projectUserRepository.getAllProjectUserByProject(projectId);
                modifyAssociatedTeams(projectId, projectBody, oldProjectTeamList);
                modifyProjectUsers(projectBody, oldProjectUserList);
                projectRepository.save(projectUpdated);
                return getProjectById(projectUpdated.getId());
            }
        } else {
            throw new CustomException("Project not found with id : " + projectId, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Permet de supprimer un projet et tous les éléments qui sont rattachés à lui
     *
     * @param projectId id du projet
     */
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

    // =========================
    // === Project user role ===
    // =========================

    /**
     * Permet de modifier le role d'un utilisateur au cours d'un projet
     *
     * @param projectId    id du projet
     * @param userId       id du user
     * @param userRoleBody id du role
     * @return le lien entre le projet et l'utilisateur mis à jour
     */
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
            projectUserUpdated.setUserRoleId(userRoleBody.getUserRole().getId());
            return projectUserRepository.save(projectUserUpdated);
        }
    }

    // ====================
    // === Project team ===
    // ====================

    /**
     * Permet d'ajouter les équipes au projet
     *
     * @param projectId id du projet
     */
    private void addTeamsToProject(int projectId) {
        Project project = projectRepository.getById(projectId);
        project.setProjectTeams(teamRepository.getTeamsByProject(projectId));
    }

    /**
     * Permet de récupérer les équipes qui sont libres avec leurs utilisateurs
     *
     * @param teamsId id de l'équipe
     * @return une liste d'équipes libres
     */
    private List<Team> getFreeTeams(List<Integer> teamsId) {
        List<Team> freeTeams = teamService.getFreeTeams(teamsId);
        if (teamsId.size() > freeTeams.size()) {
            throw new CustomException("You have provided one or more team already associated to a project", HttpStatus.BAD_REQUEST);
        }
        return freeTeams;
    }

    /**
     * Permet de vérifier que les utilisateurs sont au moins dans une équipe du projet
     *
     * @param projectUsers les utilisateurs
     * @param freeTeams    les équipes
     */
    private void verifyIfUsersExistsInTeams(List<ProjectUser> projectUsers, List<Team> freeTeams) {
        projectUsers.forEach(projectUser -> {
            if (!userService.isUserInTeams(projectUser.getUserId(), freeTeams)) {
                throw new CustomException("User with id : " + projectUser.getUserId() + " is not in any team that you have provided", HttpStatus.BAD_REQUEST);
            }
        });
    }

    /**
     * Vérifie si les équipes existent et si elles ne sont pas rattachées à un autre projet
     * pendant la modification du projet
     *
     * @param projectId   id du projet
     * @param projectBody objet du projet avec les équipes
     */
    private void verificationsOnTeamsDuringProjectUpdate(int projectId, ProjectBody projectBody) {
        projectBody.getTeamIdList().forEach(teamId -> {
            if (teamRepository.findById(teamId).isEmpty()) {
                throw new CustomException("Team with id : " + teamId + " doesn't exist", HttpStatus.BAD_REQUEST);
            } else {
                Team team = teamRepository.findById(teamId).get();
                if (team.getProjectId() != projectId) {
                    throw new CustomException("Team with id : " + teamId + " is already associated to a project", HttpStatus.CONFLICT);
                }
            }
        });
    }

    /**
     * Met à jour les équipes associées au projet durant la modification de celui-ci
     *
     * @param projectId          id du projet
     * @param projectBody        objet du projet
     * @param oldProjectTeamList anciennes équipes rattachées au projet avant modification
     */
    private void modifyAssociatedTeams(int projectId, ProjectBody projectBody, List<Team> oldProjectTeamList) {
        projectBody.getTeamIdList().forEach(teamId -> {
            if (oldProjectTeamList.stream().noneMatch(team -> teamId == team.getId())) {
                projectRepository.addProjectToTeam(teamId, projectId);
            }
        });

        oldProjectTeamList.forEach(team -> {
            if (!projectBody.getTeamIdList().contains(team.getId())) {
                teamRepository.removeProjectIdFromTeam(projectId, team.getId());
            }
        });
    }

    // ====================
    // === Project user ===
    // ====================

    /**
     * Permet d'ajouter un utilisateur à un projet
     *
     * @param projectId    id du projet
     * @param projectUsers les utilisateurs et leur rôle
     */
    private void addProjectUsersToProject(int projectId, List<ProjectUser> projectUsers) {
        for (ProjectUser projectUser : projectUsers) {
            projectUser.setProjectId(projectId);
            projectUserRepository.save(projectUser);
        }
    }

    /**
     * Vérifie si les utilisateurs existent et s'ils sont bien rattachés à au moins une équipe
     * pendant la modification du projet
     *
     * @param projectBody objet du projet avec ses utilisateurs
     */
    private void verificationsOnUsersDuringProjectUpdate(ProjectBody projectBody) {
        projectBody.getProjectUsers().forEach(projectUser -> {
            if (userRepository.findById(projectUser.getUserId()).isEmpty()) {
                throw new CustomException("User with id : " + projectUser.getUserId() + " doesn't exists", HttpStatus.BAD_REQUEST);
            } else {
                List<Team> teamList = new ArrayList<>();
                projectBody.getTeamIdList().forEach(teamId -> {
                    Team team = teamRepository.getById(teamId);
                    team.setUsers(userRepository.findAllUsersByTeamId(teamId));
                    teamList.add(team);
                });
                if (!userService.isUserInTeams(projectUser.getUserId(), teamList)) {
                    throw new CustomException("User with id : " + projectUser.getUserId() + " is not in any team that you have provided", HttpStatus.BAD_REQUEST);
                }
            }
        });
    }

    /**
     * Met à jour les utilisateurs du projet durant sa modification
     *
     * @param projectBody        objet du projet
     * @param oldProjectUserList anciens utilisateurs rattachés au projet avant modification
     */
    private void modifyProjectUsers(ProjectBody projectBody, List<ProjectUser> oldProjectUserList) {
        projectBody.getProjectUsers().forEach(projectUser -> {
            if (oldProjectUserList.stream().noneMatch(oldProjectUser -> projectUser.getUserId() == oldProjectUser.getUserId())) {
                projectUserRepository.save(projectUser);
            }
        });

        oldProjectUserList.forEach(oldProjectUser -> {
            if (projectBody.getProjectUsers().stream().noneMatch(projectUser -> oldProjectUser.getUserId() == projectUser.getUserId())) {
                projectUserRepository.delete(oldProjectUser);
            }
        });
    }

    /**
     * Vérifie si l'utilisateur courant est le gestionnaire du projet
     *
     * @param projectId id du projet
     */
    private void verifyProjectOwnership(int projectId) {
        User projectOwner = userRepository.getProjectOwner(projectId);
        if (Common.GetCurrentUser().getId() != projectOwner.getId()) {
            throw new CustomException("You are not project owner, you can't perform this action", HttpStatus.UNAUTHORIZED);
        }
    }
}
