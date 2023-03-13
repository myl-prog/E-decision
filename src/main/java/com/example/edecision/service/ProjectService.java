package com.example.edecision.service;

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
import com.example.edecision.service.TeamService;
import com.example.edecision.service.UserService;
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
    public ProjectRepository projectRepo;

    @Autowired
    public ProjectStatusRepository projectStatusRepo;

    @Autowired
    public TeamRepository teamRepo;

    @Autowired
    public ProjectUserRepository projectUserRepo;

    @Autowired
    public UserRepository userRepo;

    @Autowired
    public TeamService teamService;

    @Autowired
    public UserService userService;

    // ===============
    // === Project ===
    // ===============

    /**
     * Permet de récupérer tous les projets
     */
    public List<Project> getAllProjects() {
        List<Project> allProjects = projectRepo.findAll();
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
        Optional<Project> optionalProject = projectRepo.findById(projectId);
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
        if (projectStatusRepo.findById(projectBody.getProject().getProjectStatus().getId()).isPresent()) {
            ProjectStatus projectStatus = projectStatusRepo.findById(projectBody.getProject().getProjectStatus().getId()).get();
            projectBody.getProject().setProjectStatus(projectStatus);
            List<Team> freeTeams = getFreeTeams(projectBody.getTeamIdList());
            verifyIfUsersExistsInTeams(projectBody.getProjectUsers(), freeTeams);

            Project createdProject = projectRepo.save(projectBody.getProject());
            teamRepo.addProjectToTeams(projectBody.getTeamIdList(), projectBody.getProject().getId());
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
        if (projectRepo.findById(projectId).isPresent()) {
            if (projectStatusRepo.findById(projectBody.getProject().getProjectStatus().getId()).isEmpty()) {
                throw new CustomException("This project status does not exist", HttpStatus.BAD_REQUEST);
            } else {
                Project projectUpdated = projectRepo.findById(projectId).get();
                projectUpdated.setTitle(projectBody.getProject().getTitle());
                projectUpdated.setDescription(projectBody.getProject().getDescription());
                projectUpdated.setProjectStatus(projectBody.getProject().getProjectStatus());

                verificationsOnTeamsDuringProjectUpdate(projectId, projectBody);
                verificationsOnUsersDuringProjectUpdate(projectBody);

                List<Team> oldProjectTeamList = teamRepo.getTeamsByProject(projectId);
                List<ProjectUser> oldProjectUserList = projectUserRepo.getAllProjectUserByProject(projectId);
                modifyAssociatedTeams(projectId, projectBody, oldProjectTeamList);
                modifyProjectUsers(projectBody, oldProjectUserList);
                projectRepo.save(projectUpdated);
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
        if (projectRepo.findById(projectId).isPresent()) {
            verifyProjectOwnership(projectId);
            projectUserRepo.deleteAllProjectUserByProjectId(projectId);
            teamRepo.removeProjectIdFromTeams(projectId);
            projectRepo.deleteById(projectId);
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
        if (projectRepo.findById(projectId).isEmpty()) {
            throw new CustomException("Project not found with id : " + projectId, HttpStatus.NOT_FOUND);
        }
        if (userRepo.findById(userId).isEmpty()) {
            throw new CustomException("User not found with id : " + userId, HttpStatus.NOT_FOUND);
        }
        if (projectUserRepo.findProjectUserByProjectIdAndUserId(projectId, userId).isEmpty()) {
            throw new CustomException("This user is not associated to this project", HttpStatus.BAD_REQUEST);
        } else {
            ProjectUser projectUserUpdated = projectUserRepo.findProjectUserByProjectIdAndUserId(projectId, userId).get();
            projectUserUpdated.setUserRoleId(userRoleBody.getUserRole().getId());
            return projectUserRepo.save(projectUserUpdated);
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
        Project project = projectRepo.getById(projectId);
        project.setProjectTeams(teamRepo.getTeamsByProject(projectId));
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
            if (teamRepo.findById(teamId).isEmpty()) {
                throw new CustomException("Team with id : " + teamId + " doesn't exist", HttpStatus.BAD_REQUEST);
            } else {
                Team team = teamRepo.findById(teamId).get();
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
                teamRepo.addProjectToTeam(teamId, projectId);
            }
        });

        oldProjectTeamList.forEach(team -> {
            if (!projectBody.getTeamIdList().contains(team.getId())) {
                teamRepo.removeProjectIdFromTeam(projectId, team.getId());
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
            projectUserRepo.save(projectUser);
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
            if (userRepo.findById(projectUser.getUserId()).isEmpty()) {
                throw new CustomException("User with id : " + projectUser.getUserId() + " doesn't exists", HttpStatus.BAD_REQUEST);
            } else {
                List<Team> teamList = new ArrayList<>();
                projectBody.getTeamIdList().forEach(teamId -> {
                    Team team = teamRepo.getById(teamId);
                    team.setUsers(userRepo.findAllUsersByTeamId(teamId));
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
                projectUserRepo.save(projectUser);
            }
        });

        oldProjectUserList.forEach(oldProjectUser -> {
            if (projectBody.getProjectUsers().stream().noneMatch(projectUser -> oldProjectUser.getUserId() == projectUser.getUserId())) {
                projectUserRepo.delete(oldProjectUser);
            }
        });
    }

    /**
     * Vérifie si l'utilisateur courant est le gestionnaire du projet
     *
     * @param projectId id du projet
     */
    private void verifyProjectOwnership(int projectId) {
        User projectOwner = userRepo.getProjectOwner(projectId);
        if (Common.GetCurrentUser().getId() != projectOwner.getId()) {
            throw new CustomException("You are not project owner, you can't perform this action", HttpStatus.UNAUTHORIZED);
        }
    }

    // ======================
    // === Project status ===
    // ======================

    /**
     * Permet de récupérer la liste des statuts de projet
     *
     * @return la liste des statuts de projet
     */
    public List<ProjectStatus> getProjectStatus() {
        return projectStatusRepo.findAll();
    }
}
