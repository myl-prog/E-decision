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
    public UserService service;
    @GetMapping("/users")
    public List<User> getAll(){
        return service.getAll();
    }
    @GetMapping("/users/{id}")
    public User getById(@PathVariable("id") String id){
        return service.getById(id);
    }
    @PostMapping("/Users/add/{id}")
    public void addUser(@PathVariable("id") String id){
        User randomUser = new User();
        randomUser.setId(id);
        randomUser.setFirst_name("firstName");
        randomUser.setLast_name("lastName");
        service.addUser(randomUser);
    }
    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("id") String id){
        return service.deleteUser(id);
    }


}
