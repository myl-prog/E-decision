package com.example.edecision.controller.team;

import com.example.edecision.model.team.Team;
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

    @GetMapping("/Teams")
    public ResponseEntity<List<Team>> getAll() {
        try {
            return new ResponseEntity<>(teamService.getAllTeams(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/Teams/{id}")
    public ResponseEntity<Team> getById(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(teamService.getTeam(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/Team/add/{name}/{project_id}/{team_type_id}")
    public ResponseEntity<Team> postTeam(@PathVariable("name") String name,@PathVariable("project_id") int project_id,@PathVariable("team_type_id") int team_type_id) {

        Team newTeam=new Team(name,project_id,team_type_id);
        try {
            return new ResponseEntity<>(teamService.createTeam(newTeam), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/Teams/delete/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("id") int id) {
        try {
            teamService.deleteTeam(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/Team/{team_id}/User/{user_id}/")
    public ResponseEntity<Team> addUser(@PathVariable("team_id") int team_id,@PathVariable("user_id") int user_id) {

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
