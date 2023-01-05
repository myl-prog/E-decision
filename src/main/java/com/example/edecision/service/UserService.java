package com.example.edecision.service;

import com.example.edecision.model.User;
import com.example.edecision.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(int id) {
        return userRepository.findById(id).get();
    }

    public User createUser(User user) {
        User newUser =  userRepository.save(user);
        System.out.println(newUser);
        return newUser;
    }

    public User updateUser(User user, int id) {
        User userUpdated = userRepository.findById(id).get();
        userUpdated.setLogin(user.getLogin());
        userUpdated.setFirst_name(user.getFirst_name());
        userUpdated.setLast_name(user.getLast_name());
        userUpdated.setPassword(user.getPassword());
        return userRepository.save(userUpdated);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
