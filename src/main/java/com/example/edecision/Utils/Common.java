package com.example.edecision.Utils;

import com.example.edecision.model.user.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class Common {

    public static User GetCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
