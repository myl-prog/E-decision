package com.example.edecision.service.user;

import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.user.User;
import com.example.edecision.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(int id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException("User not found with id : " + id, HttpStatus.NOT_FOUND));
    }

    public User createUser(User user) {
        if (userRepository.findByLogin(user.getLogin()).isPresent()) {
            throw new CustomException("User with same login already exist", HttpStatus.CONFLICT);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(User user, int id) {
        if (userRepository.findById(id).isPresent()) {
            User userUpdated = userRepository.findById(id).get();
            userUpdated.setLogin(user.getLogin());
            userUpdated.setFirst_name(user.getFirst_name());
            userUpdated.setLast_name(user.getLast_name());
            userUpdated.setPassword(user.getPassword());

            if (userRepository.findByLogin(userUpdated.getLogin()).isPresent()) {
                throw new CustomException("User with same login already exist", HttpStatus.CONFLICT);
            }
            return userRepository.save(userUpdated);
        }
        throw new CustomException("User not found with id : " + id, HttpStatus.NOT_FOUND);
    }

    public void deleteUser(int id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new CustomException("User not found with id : " + id, HttpStatus.NOT_FOUND);
        }
    }
}
