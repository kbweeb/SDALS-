package com.cardsystem.smartdoor.controller;

import com.cardsystem.smartdoor.dto.Dtos.CredentialDto;
import com.cardsystem.smartdoor.dto.Dtos.UserDto;
import com.cardsystem.smartdoor.dto.Dtos.UserRequest;
import com.cardsystem.smartdoor.service.CredentialService;
import com.cardsystem.smartdoor.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CredentialService credentialService;

    public UserController(UserService userService, CredentialService credentialService) {
        this.userService = userService;
        this.credentialService = credentialService;
    }

    @GetMapping
    public List<UserDto> list() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable int id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserRequest req) {
        return userService.create(req);
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable int id, @Valid @RequestBody UserRequest req) {
        return userService.update(id, req);
    }

    @PostMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable int id) {
        userService.deactivate(id);
    }

    @GetMapping("/{id}/credentials")
    public List<CredentialDto> credentials(@PathVariable int id) {
        return credentialService.findByUser(id);
    }
}
