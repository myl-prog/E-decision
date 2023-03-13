package com.example.edecision.controller;

import com.example.edecision.model.project.ProjectStatus;
import com.example.edecision.model.proposition.PropositionStatus;
import com.example.edecision.model.team.TeamType;
import com.example.edecision.model.user.UserRole;
import com.example.edecision.model.vote.VoteType;
import com.example.edecision.service.ProjectService;
import com.example.edecision.service.PropositionService;
import com.example.edecision.service.TeamService;
import com.example.edecision.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SettingController {

    @Autowired
    public ProjectService projectService;
    @Autowired
    public PropositionService propositionService;
    @Autowired
    public TeamService teamService;
    @Autowired
    public UserService userService;

    @GetMapping("/project-status")
    public ResponseEntity<List<ProjectStatus>> getAllProjectStatus() {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getProjectStatus());
    }

    @GetMapping("/proposition-status")
    public ResponseEntity<List<PropositionStatus>> getAllPropositionStatus() {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.getPropositionStatus());
    }

    @GetMapping("/team-types")
    public ResponseEntity<List<TeamType>> getAllTeamTypes() {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getTeamTypes());
    }

    @GetMapping("/user-roles")
    public ResponseEntity<List<UserRole>> getAllUserRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserRoles());
    }

    @GetMapping("/vote-types")
    public ResponseEntity<List<VoteType>> getAllVoteTypes() {
        return ResponseEntity.status(HttpStatus.OK).body(propositionService.getVoteTypes());
    }

}
