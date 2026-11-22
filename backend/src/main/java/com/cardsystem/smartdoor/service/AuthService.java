package com.cardsystem.smartdoor.service;

import com.cardsystem.smartdoor.dto.Dtos.LoginRequest;
import com.cardsystem.smartdoor.dto.Dtos.LoginResponse;
import com.cardsystem.smartdoor.model.Role;
import com.cardsystem.smartdoor.model.User;
import com.cardsystem.smartdoor.repository.UserRepository;
import com.cardsystem.smartdoor.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** Only active admins with a password can log into the dashboard. */
    public LoginResponse login(LoginRequest req) {
        User user = users.findByUsername(req.username())
                .filter(User::isActive)
                .filter(u -> u.getRole() == Role.ADMIN)
                .filter(u -> u.getPasswordHash() != null && passwordEncoder.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        return jwtService.issue(user);
    }
}
