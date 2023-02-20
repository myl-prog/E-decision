package com.example.edecision.controller.team;

import com.example.edecision.model.team.Team;
import com.example.edecision.model.team.TeamBody;
import com.example.edecision.service.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TeamController {
    @Autowired
    public TeamService teamService;

    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getAllTeams());
    }

    @GetMapping("/teams/{id}")
    public ResponseEntity<Team> getById(@PathVariable("id") int id) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getTeamById(id));
    }

    @PostMapping("/teams")
    public ResponseEntity<Team> createTeam(@RequestBody TeamBody teamBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(teamBody));
    }

    @PutMapping("/teams/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable("id") int teamId, @RequestBody TeamBody teamBody) {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.updateTeam(teamId, teamBody));
    }

    @DeleteMapping("teams/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("id") int id) {
        teamService.deleteTeam(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/Team/{team_id}/User/{user_id}/")
    public ResponseEntity<Team> addUser(@PathVariable("team_id") int team_id, @PathVariable("user_id") int user_id) {

        /*UserTeam user_team= new UserTeam(team_id,user_id);
        try {
            return new ResponseEntity<>(teamService.createTeam(newTeam), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        */
        return null;
    }
}
