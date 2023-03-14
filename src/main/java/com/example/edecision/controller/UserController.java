package com.example.edecision.controller;

import com.example.edecision.model.team.Team;
import com.example.edecision.model.user.User;
import com.example.edecision.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    public UserService userService;

    // ============
    // === User ===
    // ============

    @GetMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Récupère l'ensemble des utilisateurs",
            notes = "Utilisable pour le formulaire de création et modification d'une équipe, d'un projet ou d'une proposition")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Les utilisateurs sont bien retournés", response = User.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur"),
    })
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @GetMapping(value = "/users/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Récupère un utilisateur avec son identifiant",
            notes = "Utilisable visualiser un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'utilisateur est bien retourné", response = User.class),
            @ApiResponse(code = 404, message = "L'utilisateur n'existe pas"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur"),
    })
    public ResponseEntity<User> getUserById(@PathVariable("id") int id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(id));
    }

    @PutMapping(value = "/users/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modifie un utilisateur",
            notes = "Aucun utilisateur avec le même login ne doit déjà exister")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'utilisateur a bien été modifié et est retourné", response = User.class),
            @ApiResponse(code = 404, message = "L'utilisateur n'existe pas"),
            @ApiResponse(code = 409, message = "Un utilisateur existe déjà avec le même login"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur"),
    })
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable("id") int id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(user, id));
    }

    @DeleteMapping(value = "/users/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Supprime un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'utilisateur a bien été supprimé"),
            @ApiResponse(code = 404, message = "L'utilisateur n'existe pas"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur"),
    })
    public ResponseEntity<HttpStatus> deleteUserById(@PathVariable("id") int id) {
        userService.deleteUser(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    // ================
    // === Register ===
    // ================

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Créé un utilisateur",
            notes = "Aucun utilisateur avec le même login ne doit déjà exister")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "L'utilisateur a bien été créé et est retourné", response = User.class),
            @ApiResponse(code = 409, message = "Un utilisateur existe déjà avec le même login"),
            @ApiResponse(code = 500, message = "Erreur interne du serveur"),
    })
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
    }
}
