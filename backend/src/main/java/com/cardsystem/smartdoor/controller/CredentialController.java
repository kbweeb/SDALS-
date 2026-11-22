package com.cardsystem.smartdoor.controller;

import com.cardsystem.smartdoor.dto.Dtos.CredentialDto;
import com.cardsystem.smartdoor.dto.Dtos.CredentialRequest;
import com.cardsystem.smartdoor.service.CredentialService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credentials")
public class CredentialController {

    private final CredentialService credentialService;

    public CredentialController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CredentialDto create(@Valid @RequestBody CredentialRequest req) {
        return credentialService.create(req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        credentialService.delete(id);
    }
}
