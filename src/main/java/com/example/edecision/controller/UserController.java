package com.example.edecision.controller;

import com.example.edecision.model.user.User;
import com.example.edecision.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") int id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable("id") int id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(user, id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUserById(@PathVariable("id") int id) {
        userService.deleteUser(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    // ================
    // === Register ===
    // ================

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
    }
}
