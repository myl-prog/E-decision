package com.example.edecision.controller.authentication;

import com.example.edecision.model.authentication.AuthRequest;
import com.example.edecision.model.authentication.AuthResponse;
import com.example.edecision.authentication.JwtTokenUtil;
import com.example.edecision.model.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;

@RestController
public class AuthController {
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    JwtTokenUtil jwtUtil;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Permet à un utilisateur de se connecter avec son identifiant et son mot de passe",
                  notes = "Retourne l'identifiant de l'utilisateur ainsi que le jwt généré qui va permettre de s'authentifier")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'identifiant et le mot de passe sont correct", response = AuthResponse.class),
            @ApiResponse(code = 401, message = "Les informations renseignées ne sont pas valides")
    })
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
