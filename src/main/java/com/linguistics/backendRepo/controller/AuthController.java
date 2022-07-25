package com.linguistics.backendRepo.controller;
import javax.validation.Valid;

import com.linguistics.backendRepo.model.JwtResponse;
import com.linguistics.backendRepo.model.LoginRequest;
import com.linguistics.backendRepo.model.SignupRequest;
import com.linguistics.backendRepo.model.User;
import com.linguistics.backendRepo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public JwtResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.authUser(loginRequest);
    }

    @PostMapping("/signup")
    public void registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        userService.saveUser(signUpRequest);
    }
}