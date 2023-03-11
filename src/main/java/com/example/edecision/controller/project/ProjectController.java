package com.example.edecision.controller.project;

import com.example.edecision.model.amendement.Amendement;
import com.example.edecision.model.project.Project;
import com.example.edecision.model.project.ProjectBody;
import com.example.edecision.model.project.ProjectUser;
import com.example.edecision.model.amendement.AmendementBody;
import com.example.edecision.model.proposition.DeletePropositionResult;
import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.model.proposition.PropositionBody;
import com.example.edecision.model.user.UserRoleBody;
import com.example.edecision.service.amendement.AmendementService;
import com.example.edecision.service.project.ProjectService;
import com.example.edecision.service.proposition.PropositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {
    @Autowired
    public ProjectService projectService;

    @Autowired
    public PropositionService propositionService;

    @Autowired
    public AmendementService amendementService;


    // ========================
    // ======= Projects =======
    // ========================

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getAllProjects());
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable("id") int id) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getProjectById(id));
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> createProject(@RequestBody ProjectBody projectBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectBody));
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable("id") int id, @RequestBody ProjectBody projectBody) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.updateProject(id, projectBody));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<HttpStatus> deleteProject(@PathVariable("id") int id) {
        projectService.deleteProject(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ========================
    // ===== Project user =====
    // ========================

    @PatchMapping("/projects/{projectId}/users/{userId}")
    public ResponseEntity<ProjectUser> changeUserRole(@PathVariable("projectId") int projectId, @PathVariable("userId") int userId, @RequestBody UserRoleBody userRoleBody) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.changeUserRole(projectId, userId, userRoleBody));
    }

    // ========================
    // = Project propositions =
    // ========================

    @GetMapping("/projects/{projectId}/propositions/{propositionId}")
    public ResponseEntity<Proposition> getProjectPropositionById(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId) {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.getProjectPropositionById(projectId, propositionId));
    }

    @PostMapping("/projects/{projectId}/propositions")
    public ResponseEntity<Proposition> createProjectProposition(@PathVariable("projectId") int projectId, @RequestBody PropositionBody propositionBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propositionService.createProjectProposition(projectId, propositionBody));
    }

    @PutMapping("/projects/{projectId}/propositions/{propositionId}")
    public ResponseEntity<Proposition> updateProjectProposition(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @RequestBody PropositionBody propositionBody) {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.updateProjectPropositionById(projectId, propositionId, propositionBody));
    }

    @DeleteMapping("/projects/{projectId}/propositions/{propositionId}")
    public ResponseEntity<DeletePropositionResult> deleteProposition(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId) {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.deleteProjectProposition(projectId, propositionId));
    }

    // ===================================
    // = Project proposition amendements =
    // ===================================

    @GetMapping("/projects/{projectId}/propositions/{propositionId}/amendements")
    public ResponseEntity<List<Amendement>> getProjectPropositionAmendements(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId) {
        return ResponseEntity.status(HttpStatus.OK).body(amendementService.getProjectPropositionAmendements(projectId, propositionId));
    }

    @GetMapping("/projects/{projectId}/propositions/{propositionId}/amendements/{amendementId}")
    public ResponseEntity<Amendement> getProjectPropositionAmendement(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @PathVariable("amendementId") int amendementId) {
        return ResponseEntity.status(HttpStatus.OK).body(amendementService.getProjectPropositionAmendementById(projectId, propositionId, amendementId));
    }

    @PostMapping("/projects/{projectId}/propositions/{propositionId}/amendements")
    public ResponseEntity<Amendement> createProjectPropositionAmendement(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @RequestBody AmendementBody amendBody) {
        return ResponseEntity.status(HttpStatus.OK).body(amendementService.createProjectPropositionAmendement(projectId, propositionId, amendBody));
    }

    @PutMapping("/projects/{projectId}/propositions/{propositionId}/amendements/{amendementId}")
    public ResponseEntity<Amendement> updateProjectPropositionAmendement(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @PathVariable("amendementId") int amendementId, @RequestBody AmendementBody amendBody) {
        return ResponseEntity.status(HttpStatus.OK).body(amendementService.updateProjectPropositionAmendementById(projectId, propositionId, amendementId, amendBody));
    }

    @DeleteMapping("/projects/{projectId}/propositions/{propositionId}/amendements/{amendementId}")
    public ResponseEntity<HttpStatus> updateProjectPropositionAmendement(@PathVariable("projectId") int projectId, @PathVariable("propositionId") int propositionId, @PathVariable("amendementId") int amendementId) {
        amendementService.deleteProjectPropositionAmendementById(projectId, propositionId, amendementId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
