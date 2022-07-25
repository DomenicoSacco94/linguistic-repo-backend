package com.linguistics.backendRepo.service;

import com.linguistics.backendRepo.config.JwtUtils;
import com.linguistics.backendRepo.exceptions.DuplicateUsernameException;
import com.linguistics.backendRepo.model.JwtResponse;
import com.linguistics.backendRepo.model.LoginRequest;
import com.linguistics.backendRepo.model.SignupRequest;
import com.linguistics.backendRepo.model.User;
import com.linguistics.backendRepo.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    UserRepository userRepository;

    PasswordEncoder encoder;

    AuthenticationManager authenticationManager;

    JwtUtils jwtUtils;

    @Lazy
    public UserService(UserRepository userRepository, PasswordEncoder encoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public JwtResponse authUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername());
    }

    public User saveUser(SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new DuplicateUsernameException("This user already exists: " + signUpRequest.getUsername());
        }
        User user = new User(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return User.build(user);
    }
}
