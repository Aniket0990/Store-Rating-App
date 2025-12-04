package com.example.store_rating_app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.store_rating_app.entity.User;
import com.example.store_rating_app.service.AuthService;
import com.example.store_rating_app.service.JwtService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User user) {
        String message = authService.register(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            User loggedUser = authService.validateUser(user.getEmail(), user.getPassword());
            String token = jwtService.generateToken(loggedUser); // pass User object

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", loggedUser.getId());
            userData.put("name", loggedUser.getName());
            userData.put("email", loggedUser.getEmail());
            userData.put("address", loggedUser.getAddress());
            userData.put("role", loggedUser.getRole());
            response.put("user", userData);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }
}
