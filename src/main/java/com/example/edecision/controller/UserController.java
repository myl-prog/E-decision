package com.example.edecision.controller;

import com.example.edecision.model.User;
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

    @GetMapping("/users")
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public User getById(@PathVariable("id") String id) {
        return userService.getById(id);
    }

    @PostMapping("/users/add/")
    public void postUser(@RequestBody User user) {
        userService.createUser(user);
    }

    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("id") int id) {
        return userService.deleteUser(id);
    }
}
