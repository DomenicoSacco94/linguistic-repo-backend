package com.linguistics.backendRepo.controller;

import com.linguistics.backendRepo.model.JwtResponse;
import com.linguistics.backendRepo.model.LoginRequest;
import com.linguistics.backendRepo.model.SignupRequest;
import com.linguistics.backendRepo.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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