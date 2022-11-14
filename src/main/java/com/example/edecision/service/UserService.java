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
    public UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getById(String id) {
        return userRepository.findById(id).get();
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public ResponseEntity<HttpStatus> deleteUser(int id) {
        try {
            userRepository.deleteById(String.valueOf(id));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
