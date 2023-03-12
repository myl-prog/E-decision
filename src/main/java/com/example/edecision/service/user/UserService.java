package com.example.edecision.service.user;

import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.team.Team;
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

    // ============
    // === User ===
    // ============

    /**
     * Permet de récupérer l'ensemble des utilisateurs
     *
     * @return une liste d'utilisateurs
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Permet de récupérer un utilisateur avec son identifiant
     *
     * @param id id de l'utilisateur
     * @return l'utilisateur
     **/
    public User getUser(int id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException("User not found with id : " + id, HttpStatus.NOT_FOUND));
    }

    /**
     * Permet de créer un utilisateur
     *
     * @param user objet de l'utilisateur
     * @return l'utilisateur créé
     */
    public User createUser(User user) {
        if (userRepository.findByLogin(user.getLogin()).isPresent()) {
            throw new CustomException("User with same login already exist", HttpStatus.CONFLICT);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Permet de modifier un utilisateur
     *
     * @param user objet de l'utilisateur
     * @param id   id de l'utilisateur
     * @return l'utilisateur mis à jour
     */
    public User updateUser(User user, int id) {
        if (userRepository.findById(id).isPresent()) {
            User userUpdated = userRepository.findById(id).get();
            userUpdated.setLogin(user.getLogin());
            userUpdated.setFirstName(user.getFirstName());
            userUpdated.setLastName(user.getLastName());
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            if (userRepository.findByLogin(userUpdated.getLogin()).isPresent()) {
                User foundUser = userRepository.findByLogin(userUpdated.getLogin()).get();
                if (foundUser.getId() != id) {
                    throw new CustomException("User with same login already exist", HttpStatus.CONFLICT);
                }
            }
            return userRepository.save(userUpdated);
        }
        throw new CustomException("User not found with id : " + id, HttpStatus.NOT_FOUND);
    }

    /**
     * Permet de supprimer un utilisateur
     *
     * @param id id de l'utilisateur
     */
    public void deleteUser(int id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new CustomException("User not found with id : " + id, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Vérifier si un u'itlisateur est dans au moins une des équipes
     *
     * @param userId    id de l'utilisateur
     * @param freeTeams liste des équipes
     * @return un booléen qui indique si oui ou non il est dans une équipe
     */
    public boolean isUserInTeams(int userId, List<Team> freeTeams) {
        return freeTeams.stream().anyMatch(team -> team.getUsers().stream().anyMatch(user -> user.getId() == userId));
    }
}
