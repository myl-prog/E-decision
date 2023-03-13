package com.example.edecision.service.user;

import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.team.Team;
import com.example.edecision.model.team.TeamType;
import com.example.edecision.model.user.User;
import com.example.edecision.model.user.UserRole;
import com.example.edecision.repository.user.UserRepository;
import com.example.edecision.repository.user.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepo;

    @Autowired
    public UserRoleRepository userRoleRepo;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ============
    // === User ===
    // ============

    /**
     * Permet de récupérer l'ensemble des utilisateurs
     *
     * @return une liste d'utilisateurs
     */
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Permet de récupérer un utilisateur avec son identifiant
     *
     * @param id id de l'utilisateur
     * @return l'utilisateur
     **/
    public User getUser(int id) {
        return userRepo.findById(id).orElseThrow(() -> new CustomException("User not found with", HttpStatus.NOT_FOUND));
    }

    /**
     * Permet de créer un utilisateur
     *
     * @param user objet de l'utilisateur
     * @return l'utilisateur créé
     */
    public User createUser(User user) {
        if (userRepo.findByLogin(user.getLogin()).isPresent())
            throw new CustomException("User with same login already exist", HttpStatus.CONFLICT);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    /**
     * Permet de modifier un utilisateur
     *
     * @param user objet de l'utilisateur
     * @param id   id de l'utilisateur
     * @return l'utilisateur mis à jour
     */
    public User updateUser(User user, int id) {
        Optional<User> optionalUser = userRepo.findById(id);
        Optional<User> optionalUserWithSameLogin = userRepo.findByLogin(user.getLogin());

        if (optionalUser.isEmpty())
            throw new CustomException("User not found with this id", HttpStatus.NOT_FOUND);

        // Ici on vérifie si un utilisateur avec le meme login n'existe pas deja, sauf si c'est l'utilisateur en train d'être modifié.
        if (optionalUserWithSameLogin.isPresent() && optionalUserWithSameLogin.get().getId() != id)
            throw new CustomException("User with same login already exist", HttpStatus.CONFLICT);

        User userUpdated = optionalUser.get();
        userUpdated.setLogin(user.getLogin());
        userUpdated.setFirstName(user.getFirstName());
        userUpdated.setLastName(user.getLastName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(userUpdated);
    }

    /**
     * Permet de supprimer un utilisateur
     *
     * @param id id de l'utilisateur
     */
    public void deleteUser(int id) {
        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isEmpty())
            throw new CustomException("User not found with this id", HttpStatus.NOT_FOUND);

        userRepo.deleteById(id);
    }

    /**
     * Vérifie si un utilisateur est dans au moins une des équipes
     *
     * @param userId    id de l'utilisateur
     * @param freeTeams liste des équipes
     * @return un booléen qui indique si oui ou non il est dans une équipe
     */
    public boolean isUserInTeams(int userId, List<Team> freeTeams) {
        return freeTeams.stream().anyMatch(team -> team.getUsers().stream().anyMatch(user -> user.getId() == userId));
    }

    // =================
    // === User role ===
    // =================

    /**
     * Permet de récupérer la liste des rôles d'utilisateur
     *
     * @return la liste des rôles d'utilisateur
     */
    public List<UserRole> getUserRoles() {
        return userRoleRepo.findAll();
    }
}
