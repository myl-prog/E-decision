package com.example.edecision.service.user;

import com.example.edecision.authentication.JwtTokenUtil;
import com.example.edecision.model.user.User;
import com.example.edecision.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(int id) {
        return userRepository.findById(id).get();
    }

    public User createUser(User user) {
        if (userRepository.findByLogin(user.getLogin()).isPresent()) {
            throw new IllegalArgumentException();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
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
