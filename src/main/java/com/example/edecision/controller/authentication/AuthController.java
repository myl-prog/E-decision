package com.example.edecision.controller.authentication;

import com.example.edecision.model.authentication.AuthRequest;
import com.example.edecision.model.authentication.AuthResponse;
import com.example.edecision.authentication.JwtTokenUtil;
import com.example.edecision.model.user.User;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    JwtTokenUtil jwtUtil;


    @ApiOperation(value = "Permet à un utilisateur de se connecter avec son identifiant et son mot de passe", notes = "Retourne l'identifiant de l'utilisateur ainsi que le jwt généré qui va permettre de s'authentifier")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            AuthResponse response = new AuthResponse(user.getLogin(), accessToken);

            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
