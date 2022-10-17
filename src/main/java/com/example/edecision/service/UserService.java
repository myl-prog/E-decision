package com.example.edecision.service;
import com.example.edecision.model.User;
import com.example.edecision.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    public UserRepository repo;
    public List<User> getAll(){
        return repo.findAll();
    }
}
