package com.example.edecision.controller;
import com.example.edecision.model.User;
import com.example.edecision.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    public UserService service;
    @GetMapping("/users")
    public List<User> getAll(){
        return service.getAll();
    }
}
