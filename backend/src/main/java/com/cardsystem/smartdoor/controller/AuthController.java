package com.cardsystem.smartdoor.controller;

import com.cardsystem.smartdoor.dto.Dtos.LoginRequest;
import com.cardsystem.smartdoor.dto.Dtos.LoginResponse;
import com.cardsystem.smartdoor.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}
