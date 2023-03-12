package com.example.edecision.utils;

import com.example.edecision.model.user.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static java.util.Date.from;

public class Common {

    /**
     * Récupère l'utilisateur qui appelle l'api grâce à son token jwt
     *
     * @return l'utilisateur
     */
    public static User GetCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Récupère la date courante
     *
     * @return la date
     */
    public static Date GetCurrentLocalDate(){
        LocalDateTime localDateTime = LocalDateTime.now();
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
