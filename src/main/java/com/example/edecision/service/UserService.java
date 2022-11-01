package com.example.edecision.service;
import com.example.edecision.model.User;
import com.example.edecision.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    public UserRepository repo;


    public List<User> getAll(){
        return repo.findAll();
    }

    public User getById(String id){
        return repo.findById(id).get();
    }
    public User addUser(User user){
        return repo.save(user);
    }
    public ResponseEntity<HttpStatus> deleteUser(String id){
        try {
            repo.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
